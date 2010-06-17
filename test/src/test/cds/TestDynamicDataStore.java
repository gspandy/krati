package test.cds;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import krati.cds.impl.segment.SegmentFactory;
import krati.cds.impl.store.DynamicDataStore;
import krati.cds.store.DataStore;
import krati.util.HashFunction;
import test.AbstractTest;

/**
 * TestDynamicDataStore using MemorySegment.
 * 
 * @author jwu
 *
 */
public class TestDynamicDataStore extends AbstractTest
{
    public TestDynamicDataStore()
    {
        super(TestDynamicDataStore.class.getName());
    }
    
    protected SegmentFactory getSegmentFactory()
    {
        return new krati.cds.impl.segment.MemorySegmentFactory();
    }
    
    protected DynamicDataStore getDynamicDataStore(File storeDir, int initLevel, int segFileSizeMB) throws Exception
    {
        return new DynamicDataStore(storeDir, initLevel, 10000, 5, segFileSizeMB, getSegmentFactory(), new HashFunctionInteger());
    }
    
    public void testCapacityGrowth() throws Exception
    {
        System.out.println("--- testCapacityGrowth ---");
        
        // Create DynamicDataStore 1
        File storeDir = new File(TEST_OUTPUT_DIR, getClass().getSimpleName());
        DynamicDataStore dynStore1 = getDynamicDataStore(storeDir, 0, 100);
        
        System.out.println("create dynStore1"); 
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        
        int keyStart;
        int keyCount;
        int unitCapacity = dynStore1.getUnitCapacity();
        
        keyStart = 0;
        keyCount = unitCapacity;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 2) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 2));
        
        keyStart += keyCount;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 3) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 3));
        
        keyStart += keyCount;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 4) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 4));
        
        keyStart += keyCount;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 5) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 5));
        
        keyStart += keyCount;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 6) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 6));
        
        dynStore1.rehash();
        System.out.println("rehash()");
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if(dynStore1.getLevel() != 3)
            throw new RuntimeException("level expected: " + 3);
        if(dynStore1.getSplit() != 0)
            throw new RuntimeException("split expected: " + 0);
        if(dynStore1.getLevelCapacity() != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + dynStore1.getLevelCapacity());
        
        keyStart = 0;
        keyCount = unitCapacity * 16;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 18) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 18));
        
        keyStart = 0;
        keyCount = unitCapacity * 8;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity * 26) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity * 26));
        
        dynStore1.rehash();
        System.out.println("rehash()");
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if(dynStore1.getLevel() != 5)
            throw new RuntimeException("level expected: " + 5);
        if(dynStore1.getSplit() != 0)
            throw new RuntimeException("split expected: " + 0);
        if ((dynStore1.getLevelCapacity()) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + dynStore1.getLevelCapacity());
        
        // Bring loadRatio up to 75%
        keyStart = unitCapacity << 5;
        keyCount = (int)(dynStore1.getCapacity() * 0.75);
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity << 5) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity << 5));
        
        // Add one more update to trigger split
        keyStart += keyCount;
        keyCount = 1;
        write(keyStart, keyCount, dynStore1);
        System.out.printf("update keyStart=%d keyCount=%d%n", keyStart, keyCount);
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if ((unitCapacity << 5 + unitCapacity) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + (unitCapacity << 5 + unitCapacity));
        
        dynStore1.rehash();
        System.out.println("rehash()");
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore1.getLevel(), dynStore1.getSplit(), dynStore1.getCapacity(), dynStore1.getLoadRatio());
        if(dynStore1.getLevel() != 6)
            throw new RuntimeException("level expected: " + 6);
        if(dynStore1.getSplit() != 0)
            throw new RuntimeException("split expected: " + 0);
        if ((dynStore1.getLevelCapacity()) != dynStore1.getCapacity())
            throw new RuntimeException("capacity expected: " + dynStore1.getLevelCapacity());
        
        dynStore1.sync();
        int capacity1 = dynStore1.getCapacity();
        
        // Create DynamicDataStore 2
        DynamicDataStore dynStore2 = getDynamicDataStore(storeDir, 0, 100);
        System.out.println("create dynStore2");
        System.out.printf("level=%d split=%d capacity=%d loadRatio=%f%n",
                dynStore2.getLevel(), dynStore2.getSplit(), dynStore2.getCapacity(), dynStore2.getLoadRatio());
        int capacity2 = dynStore2.getCapacity();
        
        // Check capacity
        if (capacity1 != capacity2)
            throw new RuntimeException("DynamicDataStore 1 and 2 have different capacities: " + capacity1 + ":" + capacity2);
        
        // Compare two data stores
        boolean b = true;
        for(int i = 0, cnt = dynStore1.getCapacity(); i < cnt; i++)
        {
            byte[] value1 = get(i, dynStore1);
            byte[] value2 = get(i, dynStore2);
            
            if(value1 == null && value2 == null)
            {
                continue;
            }
            
            if(value1 != null && value2 != null)
            {
                b = Arrays.equals(value1, value2);
            }
            else
            {
                b = false;
            }
            
            if(!b)
            {
                throw new RuntimeException("DynamicDataStore 1 and 2 differ at key=" + i
                        + " value1=" + (value1 == null ? "null" : new String(value1))
                        + " value2=" + (value2 == null ? "null" : new String(value2)));
            }
        }
        
        // Clean test output
        cleanTestOutput();
    }
    
    public void testUpdates() throws Exception
    {
        System.out.println("--- testUpdates ---");
        
        // Create DynamicDataStore 1
        File storeDir = new File(TEST_OUTPUT_DIR, getClass().getSimpleName());
        DynamicDataStore dynStore = getDynamicDataStore(storeDir, 0, 100);

        checkRandomPuts(dynStore, 1.0);
        checkRandomPuts(dynStore, 0.1);
        checkRandomPuts(dynStore, 0.2);
        checkRandomPuts(dynStore, 0.5);
        
        checkRandomDeletes(dynStore, 0.1);
        checkRandomDeletes(dynStore, 0.2);
        checkRandomDeletes(dynStore, 0.3);
        
        cleanTestOutput();
    }
    
    public void testClear() throws Exception
    {
        System.out.println("--- testClear ---");
        
        // Create DynamicDataStore 1
        File storeDir = new File(TEST_OUTPUT_DIR, getClass().getSimpleName());
        DynamicDataStore dynStore = getDynamicDataStore(storeDir, 0, 100);
        
        checkRandomPuts(dynStore, 0.1);
        checkRandomPuts(dynStore, 0.1);
        dynStore.clear();
        
        System.out.println(dynStore.getStatus());
        
        checkRandomPuts(dynStore, 0.1);
        checkRandomPuts(dynStore, 0.3);
        checkRandomPuts(dynStore, 0.3);
        dynStore.sync();
        dynStore.clear();
        
        System.out.println(dynStore.getStatus());
        
        checkRandomPuts(dynStore, 0.3);
        checkRandomPuts(dynStore, 0.5);
        checkRandomDeletes(dynStore, 0.3);
        dynStore.sync();
        dynStore.clear();
        
        cleanTestOutput();
    }
    
    private byte[] intByteArray = new byte[4];
    private ByteBuffer intByteBuffer = ByteBuffer.wrap(intByteArray);
    
    private byte[] get(int key, DataStore<byte[], byte[]> dataStore)
    {
        intByteBuffer.clear();
        intByteBuffer.putInt(key);
        return dataStore.get(intByteArray);
    }

    private void put(int key, DataStore<byte[], byte[]> dataStore) throws Exception
    {
        intByteBuffer.clear();
        intByteBuffer.putInt(key);
        
        byte[] val = ("value." + key).getBytes();
        dataStore.put(intByteArray, val);
    }
    
    private void delete(int key, DataStore<byte[], byte[]> dataStore) throws Exception
    {
        intByteBuffer.clear();
        intByteBuffer.putInt(key);
        dataStore.delete(intByteArray);
    }
    
    private void write(int keyStart, int keyCount, DataStore<byte[], byte[]> dataStore) throws Exception
    {
        for(int i = 0; i < keyCount; i++)
        {
            int index = keyStart + i;
            
            intByteBuffer.clear();
            intByteBuffer.putInt(index);
            
            byte[] key = intByteArray;
            byte[] val = ("value." + index).getBytes();
            dataStore.put(key, val);
        }
    }
    
    private void checkRandomPuts(DynamicDataStore dynStore, double ratio) throws Exception
    {
        int capacity = dynStore.getCapacity();
        int[] keys = new int[(int)(capacity  * ratio)];
        
        Random rand = new Random(capacity);
        for(int i = 0; i < keys.length; i++)
        {
            keys[i] = rand.nextInt(capacity);
            put(keys[i], dynStore);
        }
        
        for(int i = 0; i < keys.length; i++)
        {
            byte[] val = get(keys[i], dynStore);
            if(val == null || !("value." + keys[i]).equals(new String(val)))
            {
                throw new RuntimeException("Failed at key=" + keys[i] + " value= " + (val == null ? "null" : new String(val)));
            }
        }
    }
    
    private void checkRandomDeletes(DynamicDataStore dynStore, double ratio) throws Exception
    {
        int capacity = dynStore.getCapacity();
        int[] keys = new int[(int)(capacity  * ratio)];
        
        Random rand = new Random(capacity);
        for(int i = 0; i < keys.length; i++)
        {
            keys[i] = rand.nextInt(capacity);
            delete(keys[i], dynStore);
        }
        
        for(int i = 0; i < keys.length; i++)
        {
            byte[] val = get(keys[i], dynStore);
            if(val != null)
            {
                throw new RuntimeException("Failed to delete key=" + keys[i] + " value= " + (val == null ? "null" : new String(val)));
            }
        }
    }
    
    static class HashFunctionInteger implements HashFunction<byte[]>
    {
        @Override
        public long hash(byte[] key)
        {
            return ByteBuffer.wrap(key).getInt();
        }
    }
    
    public void testHashFunction()
    {
        System.out.println("--- testHashFunction ---");
        
        int i;
        byte[] intByteArray = new byte[4];
        ByteBuffer intByteBuffer = ByteBuffer.wrap(intByteArray);
        HashFunctionInteger intHash = new HashFunctionInteger();
        
        i = 0;
        intByteBuffer.clear();
        intByteBuffer.putInt(i);
        System.out.printf("hash(%d)=%d%n", i, intHash.hash(intByteArray));
        assertEquals(i, intHash.hash(intByteArray));
        
        i = 1023;
        intByteBuffer.clear();
        intByteBuffer.putInt(i);
        System.out.printf("hash(%d)=%d%n", i, intHash.hash(intByteArray));
        assertEquals(i, intHash.hash(intByteArray));
        
        i = 65535;
        intByteBuffer.clear();
        intByteBuffer.putInt(i);
        System.out.printf("hash(%d)=%d%n", i, intHash.hash(intByteArray));
        assertEquals(i, intHash.hash(intByteArray));
        
        i = 131072;
        intByteBuffer.clear();
        intByteBuffer.putInt(i);
        System.out.printf("hash(%d)=%d%n", i, intHash.hash(intByteArray));
        assertEquals(i, intHash.hash(intByteArray));
        
        i = 262144;
        intByteBuffer.clear();
        intByteBuffer.putInt(i);
        System.out.printf("hash(%d)=%d%n", i, intHash.hash(intByteArray));
        assertEquals(i, intHash.hash(intByteArray));
    }
}
