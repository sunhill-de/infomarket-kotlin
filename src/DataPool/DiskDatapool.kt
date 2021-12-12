package sunhill.DataPool

import java.io.File

class DiskDatapool: DataPoolBase() {

    data class Disk(val name: String, val size: Long, val model: String, val state: String, val vendor: String)

    data class Partition(val name: String, val size: Long, val used: Long, val avail: Long,
                         val filesystem: String, val mountpoint: String, val ro: Boolean, val rm: Boolean)

    data class Raid(val name: String, val level: String, val devices: List<String>, val isUp: List<Boolean>,
                    val online: String,
                    val recovered: Long=0, var total: Long=0, var eta: String="", var speed: String="")

    var _disks: MutableList<Disk>? = null

    var _partitions: MutableList<Partition>? = null

    var _raids: MutableList<Raid>? = null

    val disks
        get() = if (_disks == null) {
            readInformations()
            _disks
        } else _disks

    val partitions
        get() = if (_partitions == null) {
            readInformations()
            _partitions
        } else _partitions

    val raids
        get() = if (_raids == null) {
            readInformations()
            _raids
        } else _raids

    fun getLsBlk(): String
    {
        return "lsblk -o KNAME,FSSIZE,FSAVAIL,FSUSED,FSTYPE,MODEL,MOUNTPOINT,RO,RM,SIZE,STATE,TYPE,VENDOR -Pb".runCommand() ?: ""
    }

    fun getMdstat(): String
    {
        val f = File("/proc/mdstat")
        return if (f.exists()) {
            f.readText()
        } else {
            ""
        }
    }

    protected fun getDF(): String
    {
        return "df --output".runCommand() ?: ""
    }

    private fun getValue(pair: String): String
    {
        val(_,value) = pair.split("=")
        return value.replace("\"","")
    }

    private fun getLongValue(value: String): Long
    {
        return if (value.isEmpty()) 0 else value.toLong()
    }

    private fun handleDiskEntry(map: Map<String,String>)
    {
        _disks!!.add(Disk(map["KNAME"]!!,getLongValue(map["SIZE"]!!),map["MODEL"]!!,map["STATE"]!!,map["VENDOR"]!!))
    }

    private fun handlePartititionEntry(map: Map<String,String>)
    {
        _partitions!!.add(Partition(map["KNAME"]!!,getLongValue(map["FSSIZE"]!!),getLongValue(map["FSUSED"]!!),
                                    getLongValue(map["FSAVAIL"]!!),map["FSTYPE"]!!,map["MOUNTPOINT"]!!,
                                (map["RO"]!!)=="1",(map["RM"]!!)=="1"))
    }

    private fun readLsBlk()
    {
        val lines = getLsBlk().split("\n")
        var map = mutableMapOf<String,String>()
        for (line in lines) {
          var list = line.split("\" ")
          if (list.count() >= 11) {
              map.clear()
              for (entry in list) {
                  if (entry.contains("=")) {
                      var(key,value) = entry.split("=")
                      map[key] = value.replace("\"","")
                  }
              }
              if (map["TYPE"] == "disk")
                  handleDiskEntry(map)
              else if (map["TYPE"] == "part")
                  handlePartititionEntry(map)
          }
        }
    }

    private fun readMDStat()
    {
        val lines : MutableList<String> = getMdstat().split("\n") as MutableList<String>
        var i = 1;
        while (i < lines.count()-1) {
            // lines[i] should point to the "md0 : ..." line
            // lines[i+1] should point to the [UUU] line
            // lines[i+2] is either empty, points to the bitmap line or points to the recovery line
            val device_search = Regex("[a-zA-Z0-9]+\\s:").find(lines[i])
            if (device_search != null) {
                val device = device_search.value.substring(0,device_search.value.indexOf(":")).trim()
                val(active,level) = lines[i].substring(lines[i].indexOf(":")+1).trim().split(" ")
                val disks = Regex("""[a-zA-Z0-9]+\[([0-9])+\]""").findAll(lines[i])
                val online_regex = Regex("""\[[U_]+\]""").find(lines[i+1])
                val online = if (online_regex == null) "" else online_regex.value
                val raid_devices = mutableListOf<String>()
                val raid_devices_online = mutableListOf<Boolean>()
                var j: Int = 1
                for (disk in disks) {
                    val(diskname,index) = (disk.value).split("[","]")
                    raid_devices.add(diskname)
                    val device_online = online.substring(j,j+1)
                    raid_devices_online.add(device_online.equals("U"))
                    j++
                }
                if (Regex("recovery").containsMatchIn(lines[i+2])) {
                    val block_part = Regex("[0-9]+/[0-9]+").find(lines[i+2])!!.value
                    val (restored,total) = block_part.split("/")
                    val speed_part = Regex("[a-zA-Z]+=[0-9.a-zA-Z/]+").findAll(lines[i+2])
                    val (_,speed) = speed_part.elementAt(1).value.split("=")
                    val (_,finish) = speed_part.elementAt(0).value.split("=")
                    _raids!!.add(Raid(device,level,raid_devices.toList(),raid_devices_online.toList(),active,restored.toLong(),total.toLong(),finish,speed))
                } else {
                    _raids!!.add(Raid(device,level,raid_devices.toList(),raid_devices_online.toList(),active))
                }
                if (lines[i+2].isEmpty()) {
                    i+=3
                } else {
                    i+=4
                }
            } else {
                i = lines.count()
            }
        }

    }

    protected fun readInformations()
    {
        _disks = mutableListOf<Disk>()
        _partitions = mutableListOf<Partition>()
        _raids = mutableListOf<Raid>()
        readLsBlk()
        readMDStat()
    }
}



