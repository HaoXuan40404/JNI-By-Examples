package com.webank.wedpr.ot.kkrt;

import java.nio.file.FileSystems;
import java.util.HashMap;



class NativeInterface {

    public static final int BLOCK_LONG_SIZE = 2;
    public static final int BLOCK_BYTE_SIZE = 32;
    public static final int HASH_SIZE = 20;
    public static final int SENDER_PACK_SEED_LENGTH = 49;
    public static final int RECEIVER_PACK_LENGTH = 16896;
    public static final int RECEIVER_MATRIX_COLUMN_NUMBER = 4;

    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // Windows based
            try {
                System.load(
                    FileSystems.getDefault()
                            .getPath("./build/libjnitests.dll")  // Dynamic link
                            .normalize().toAbsolutePath().toString());
            } catch (UnsatisfiedLinkError e) {
                System.load(
                    FileSystems.getDefault()
                            .getPath("./build/libjnitests.lib")  // Static link
                            .normalize().toAbsolutePath().toString());
            }
        } else {
            // Unix based
            try {
                System.load(
                    FileSystems.getDefault()
                            .getPath("/root/ppc/libOTe/lib/liblibOTe.so")  // Dynamic link
                            .normalize().toAbsolutePath().toString());
                // System.load(
                //     FileSystems.getDefault()
                //             // .getPath("./build/libjnitests.so")  // Dynamic link
                //             .getPath("./build/libjnitests.a")  // Dynamic link
                //             .normalize().toAbsolutePath().toString());
            } catch (UnsatisfiedLinkError e) {
                System.load(
                    FileSystems.getDefault()
                            .getPath("/root/ppc/libOTe/lib/liblibOTe.so")  // Dynamic link
                            .normalize().toAbsolutePath().toString());
            }
        }
    }

    // handle
    private static native long newSender(long choiceCount, long messageCount, String[] data, long[] key);
    private static native void deleteSender(long senderHandle);
    private static native void getSenderEncryptedMessage(long senderHandle, long[][] enMessage,byte[][] hash);
    private static native long newReceiver(long choiceCount, long messageCount, long[] choice);
    private static native void deleteReceiver(long receiverHandle);
    private static native void step1ReceiverInitBaseOt(long receiverHandle, byte[] senderPackSeed, long[] receiverSeed);
    private static native void step2SenderExtendSeedPack(long senderHandle, byte[] senderPackSeed, long[] receiverSeed, byte[] receiverPack);
    private static native void step3ReceiverSetSeedPack(long receiverHandle, byte[] receiverPack);
    private static native void step4SenderGenerateSeed(long senderHandle, long[] senderSeed, byte[] senderSeedHash);
    private static native void step5ReceiverInitMatrix(long receiverHandle, long[] senderSeed, byte[] senderSeedHash, long[] receiverSeed, long[] receiverMatrix);
    private static native void step6SenderSetMatrix(long senderHandle, long[] receiverSeed, long[] receiverMatrix, long[] senderSeed, long[] senderMatrix);
    // so many step7
    private static native void step7ReceiverGetFinalResultWithDecMessage(long receiverHandle, long[] senderMatrix, long[][] enMessage,byte[][] hash, String[] data);

    public static long initSender(int choiceCount, int messageCount, HashMap<Long, String> data) {
        String[] message = new String[data.size()];
        long[] key = new long[data.size()];
        int i = 0;
        for (HashMap.Entry<Long,String> entry : data.entrySet()) {
            key[i] = entry.getKey();
            message[i] = entry.getValue();
            i++;
        }
        return newSender(choiceCount, messageCount, message, key);
    }

    public static void freeSender(long senderHandle) {
        deleteSender(senderHandle);
    }

    public static OtData getSenderEncryptedMessage(long senderHandle, int messageCount) {
        OtData otData = new OtData();
        otData.enMessage = new long[messageCount][BLOCK_BYTE_SIZE];
        otData.hash = new byte[messageCount][HASH_SIZE];
        getSenderEncryptedMessage(senderHandle, otData.enMessage, otData.hash);
        return otData;
    }

    public static long initReceiver(long choiceCount, long messageCount, long[] choice) {
        return newReceiver(choiceCount, messageCount, choice);
    }

    public static void freeReceiver(long receiverHandle) {
        deleteReceiver(receiverHandle);
    }

    public static OtData step1ReceiverInitBaseOt(long receiverHandle) {
        OtData otData = new OtData();
        otData.senderPackSeed = new byte[SENDER_PACK_SEED_LENGTH];
        otData.receiverSeed = new long[BLOCK_LONG_SIZE];
        step1ReceiverInitBaseOt(receiverHandle, otData.senderPackSeed, otData.receiverSeed);
        return otData;
    }

    public static OtData step2SenderExtendSeedPack(long senderHandle, OtData otData) {
        otData.receiverPack = new byte[RECEIVER_PACK_LENGTH];
        otData.senderSeed = new long[BLOCK_LONG_SIZE];
        otData.senderSeedHash = new byte[HASH_SIZE];
        step2SenderExtendSeedPack(senderHandle, otData.senderPackSeed, otData.receiverSeed, otData.receiverPack);
        step4SenderGenerateSeed(senderHandle, otData.senderSeed, otData.senderSeedHash);
        return otData;
    }

    public static OtData step3ReceiverInitMatrix(long receiverHandle, OtData otData, int choiceCount) {
        otData.receiverSeed = new long[BLOCK_LONG_SIZE];
        int receiverMatrixSize = RECEIVER_MATRIX_COLUMN_NUMBER * BLOCK_LONG_SIZE * choiceCount;
        otData.receiverMatrix = new long[receiverMatrixSize];
        step3ReceiverSetSeedPack(receiverHandle, otData.receiverPack);
        step5ReceiverInitMatrix(receiverHandle, otData.senderSeed, otData.senderSeedHash, otData.receiverSeed, otData.receiverMatrix);
        return otData;
    }

    public static OtData step4SenderSetMatrix(long senderHandle, OtData otData, int choiceCount, int messageCount) {
        int senderMatrixSize = BLOCK_LONG_SIZE*choiceCount*messageCount;
        otData.senderMatrix = new long[senderMatrixSize];
        step6SenderSetMatrix(senderHandle, otData.receiverSeed, otData.receiverMatrix, otData.senderSeed, otData.senderMatrix);
        return otData;
    }

    public static OtResult step5ReceiverGetFinalResultWithDecMessage(long receiverHandle, OtData otData, int choiceCount) {
        OtResult otResult = new OtResult();
        otResult.data = new String[choiceCount];
        step7ReceiverGetFinalResultWithDecMessage(receiverHandle, otData.senderMatrix, otData.enMessage, otData.hash, otResult.data);
        return otResult;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // long to int
        int choiceCount = 20;
        int messageCount = 50000;
        String messages[] = new String[messageCount];
        long keys[] = new long[messageCount];
        for(int i = 0; i < messageCount; i++) {
            messages[i] = "WeDPR Test Message index:" + i;
            keys[i] = 123456 + i;
        }
    //     // byte[] s2 = {1, 2};
        long senderHandle = newSender(choiceCount, messageCount, messages, keys);
        System.out.println("senderHandle = "+ senderHandle);
        long[] choice = new long[choiceCount];
        for(int i = 0; i < choiceCount; i++) {
            choice[i] = 123476 + i;
        }
        long receiverHandle = newReceiver(choiceCount, messageCount, choice);
        System.out.println("receiverHandle = "+ receiverHandle);
        byte[] senderPackSeed = new byte[SENDER_PACK_SEED_LENGTH];
        long[] receiverSeed = new long[BLOCK_LONG_SIZE];
        step1ReceiverInitBaseOt(receiverHandle, senderPackSeed, receiverSeed);
    //     // 0~48
        // for(int i = 0; i < senderPackSeed.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderPackSeed = "+ senderPackSeed[i]);
        // }
        byte[] receiverPack = new byte[RECEIVER_PACK_LENGTH];
        step2SenderExtendSeedPack(senderHandle, senderPackSeed, receiverSeed, receiverPack);
        // for(int i = 0; i < receiverPack.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("receiverPack = "+ receiverPack[i]);
        // }
        step3ReceiverSetSeedPack(receiverHandle, receiverPack);

        long[] senderSeed = new long[BLOCK_LONG_SIZE];
        byte[] senderSeedHash = new byte[HASH_SIZE];
        step4SenderGenerateSeed(senderHandle, senderSeed, senderSeedHash);
        // for(int i = 0; i < senderSeed.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderSeed = "+ senderSeed[i]);
        // }
        // for(int i = 0; i < senderSeedHash.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderSeedHash = "+ senderSeedHash[i]);
        // }

        long[] receiverSeedKkrt = new long[BLOCK_LONG_SIZE];
        long[] receiverMatrix = new long[RECEIVER_MATRIX_COLUMN_NUMBER * BLOCK_LONG_SIZE * choiceCount];
        step5ReceiverInitMatrix(receiverHandle, senderSeed, senderSeedHash, receiverSeedKkrt, receiverMatrix);
        // for(int i = 0; i < receiverMatrix.length; i++) {
        //     System.out.println("receiverMatrix = "+ receiverMatrix[i]);
        // }
        long[] senderMatrix = new long[BLOCK_LONG_SIZE*choiceCount*messageCount];
        step6SenderSetMatrix(senderHandle, receiverSeedKkrt, receiverMatrix, senderSeed, senderMatrix);
        // for(int i = 0; i < senderMatrix.length; i++) {
        //     System.out.println("senderMatrix = "+ senderMatrix[i]);
        // }
        long[][] enMessage = new long[messageCount][BLOCK_BYTE_SIZE];
        byte[][] hash = new byte[messageCount][HASH_SIZE];
        getSenderEncryptedMessage(senderHandle, enMessage, hash);
        // for(int i = 0; i < enMessage.length; i++) {
        //     System.out.println("enMessage = "+ enMessage[i]);
        // }
        String[] data = new String[choiceCount];
        step7ReceiverGetFinalResultWithDecMessage(receiverHandle, senderMatrix, enMessage, hash, data);
        for(int i = 0; i < data.length; i++) {
            System.out.println("data = "+ data[i]);
        }
        long end = System.currentTimeMillis();
        System.out.println("choiceCount:"+ choiceCount + ", messageCount:" + messageCount + "run time = " + (end - start));

    }
}
