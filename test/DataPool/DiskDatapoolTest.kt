package sunhill.DataPool

import io.mockk.every
import io.mockk.spyk
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class DiskDatapoolTest {

    class testPool1 {

        val test: DiskDatapool

        init {
            this.test = spyk<DiskDatapool>()
            every { test.getLsBlk() } returns File("test/Data/lsblk1").readText(Charsets.UTF_8)
            every { test.getMdstat() } returns File("test/Data/mdstat1").readText(Charsets.UTF_8)
        }

        @Test
        fun testDiskCount()
        {
            assertEquals(3,test.disks!!.count())
        }

        @Test
        fun testDiskName()
        {
            assertEquals("sda",test.disks!![0].name)
        }

        @Test
        fun testDiskSize()
        {
            assertEquals(120034123776,test.disks!![0].size)
        }

        @Test
        fun testDiskModel()
        {
            assertEquals("SanDisk_SSD_PLUS_240GB",test.disks!![2].model)
        }

        @Test
        fun testDiskState()
        {
            assertEquals("running",test.disks!![2].state)
        }

        @Test
        fun testPartitionsCount()
        {
            assertEquals(11,test.partitions!!.count())
        }

        @Test
        fun testPartitionsName()
        {
            assertEquals("sda1",test.partitions!![0].name)
        }

        @Test
        fun testPartitionsSize()
        {
            assertEquals(535805952,test.partitions!![0].size)
        }

        @Test
        fun testPartitionsUsed()
        {
            assertEquals(4096,test.partitions!![0].used)
        }

        @Test
        fun testPartitionsAvail()
        {
            assertEquals(535801856,test.partitions!![0].avail)
        }

        @Test
        fun testPartitionsMountpoint()
        {
            assertEquals("/boot/efi",test.partitions!![0].mountpoint)
        }

        @Test
        fun testNoRaid()
        {
            assertEquals(0,test.raids!!.count())
        }
    }

    class testNormalRaid() {

        val test: DiskDatapool

        init {
            this.test = spyk<DiskDatapool>()
            every { test.getLsBlk() } returns File("test/Data/lsblk1").readText(Charsets.UTF_8)
            every { test.getMdstat() } returns File("test/Data/mdstat2").readText(Charsets.UTF_8)
        }

        @Test
        fun testRaidCount()
        {
            assertEquals(1,test.raids!!.count())
        }

        @Test
        fun testRaidDevicesCount()
        {
            assertEquals(3,test.raids!![0].devices.count())
        }

        @Test
        fun testRaidDeviceName()
        {
            assertEquals("sdc1",test.raids!![0].devices[1])
        }

        @Test
        fun testRaidDeviceOnline()
        {
            assertEquals(true,test.raids!![0].isUp[2])
        }

        @Test
        fun testRaidLevel()
        {
            assertEquals("raid5",test.raids!![0].level)
        }

    }

    class testRestoringRaid() {

        val test: DiskDatapool

        init {
            this.test = spyk<DiskDatapool>()
            every { test.getLsBlk() } returns File("test/Data/lsblk1").readText(Charsets.UTF_8)
            every { test.getMdstat() } returns File("test/Data/mdstat3").readText(Charsets.UTF_8)
        }

        @Test
        fun testRaidCount()
        {
            assertEquals(1,test.raids!!.count())
        }

        @Test
        fun testRaidDevicesCount()
        {
            assertEquals(6,test.raids!![0].devices.count())
        }

        @Test
        fun testRaidDeviceName()
        {
            assertEquals("sdg1",test.raids!![0].devices[1])
        }

        @Test
        fun testRaidDeviceOnline()
        {
            assertEquals(true,test.raids!![0].isUp[2])
        }

        @Test
        fun testRaidDeviceOffline()
        {
            assertEquals(false,test.raids!![0].isUp[5])
        }

        @Test
        fun testRaidLevel()
        {
            assertEquals("raid5",test.raids!![0].level)
        }

        @Test
        fun testRecovered()
        {
            assertEquals(37043392,test.raids!![0].recovered)
        }

        @Test
        fun testTotal()
        {
            assertEquals(292945152,test.raids!![0].total)
        }
    }

}