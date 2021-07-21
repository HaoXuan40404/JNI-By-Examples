package com.webank.wedpr.ot.kkrt;

import java.nio.file.FileSystems;
import java.util.HashMap;



class NativeInterface {

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
                System.out.println("try load there!!");
                System.out.println("path = "+ FileSystems.getDefault()
                .getPath("./libtest.so")  // Dynamic link
                .normalize().toAbsolutePath().toString());

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
                System.out.println("load here!!");
                System.load(
                    FileSystems.getDefault()
                            .getPath("/root/ppc/libOTe/lib/liblibOTe.so")  // Dynamic link
                            .normalize().toAbsolutePath().toString());
                System.out.println("load libtest here!!");
            }
        }
    }

    // handle
    private static native long newSender(long choiceCount, long msgCount, String[] data, long[] key);
    private static native void getSenderEncMessage(long senderHandle, long[][] enMessage,byte[][] hash);
    private static native long newReceiver(long choiceCount, long msgCount, long[] choice);
    private static native void step1ReceiverInitBaseOt(long receiverHandle, byte[] senderPackSeed, long[] receiverSeed);
    private static native void step2SenderExtendSeedPack(long senderHandle, byte[] senderPackSeed, long[] receiverSeed, byte[] receiverPack);
    private static native void step3ReceiverSetSeedPack(long receiverHandle, byte[] receiverPack);
    private static native void step4SenderGenerateSeed(long senderHandle, long[] senderSeed, byte[] senderSeedHash);
    private static native void step5ReceiverInitMatrix(long receiverHandle, long[] senderSeed, byte[] senderSeedHash, long[] receiverSeed, long[] receiverMatrix);
    private static native void step6SenderSetMatrix(long senderHandle, long[] receiverSeed, long[] receiverMatrix, long[] senderSeed, long[] senderMatrix);
    // so many step7
    private static native void step7ReceiverGetFinalResultWithDecMessage(long receiverHandle, long[] senderMatrix, long[][] enMessage,byte[][] hash, String[] data);

    public static long initSender(long choiceCount, long msgCount, HashMap<Long, String> data) {
        String[] message = new String[data.size()];
        long[] key = new long[data.size()];
        int i = 0;
        for (HashMap.Entry<Long,String> entry : data.entrySet()) {
            key[i] = entry.getKey();
            message[i] = entry.getValue();
            i++;
        }
        return newSender(choiceCount, msgCount, message, key);
    }

    public static OtData getSenderEncMessage(long senderHandle, long msgCount) {
        OtData otData = new OtData();
        otData.enMessage = new long[(int)msgCount][32];
        otData.hash = new byte[(int)msgCount][20];
        getSenderEncMessage(senderHandle, otData.enMessage, otData.hash);
        return otData;
    }

    public static long initReceiver(long choiceCount, long msgCount, long[] choice) {
        return newReceiver(choiceCount, msgCount, choice);
    }

    public static OtData step1ReceiverInitBaseOt(long receiverHandle) {
        OtData otData = new OtData();
        otData.senderPackSeed = new byte[49];
        otData.receiverSeed = new long[2];
        step1ReceiverInitBaseOt(receiverHandle, otData.senderPackSeed, otData.receiverSeed);
        return otData;
    }

    public static OtData step2SenderExtendSeedPack(long senderHandle, OtData otData) {
        otData.receiverPack = new byte[16896];
        otData.senderSeed = new long[2];
        otData.senderSeedHash = new byte[20];
        step2SenderExtendSeedPack(senderHandle, otData.senderPackSeed, otData.receiverSeed, otData.receiverPack);
        step4SenderGenerateSeed(senderHandle, otData.senderSeed, otData.senderSeedHash);
        return otData;
    }

    // public static OtData step3ReceiverSetSeedPack(long receiverHandle, OtData otData) {
    //     return otData;
    // }

    // public static OtData step4SenderGenerateSeed(long senderHandle, OtData otData) {
    //     step4SenderGenerateSeed(senderHandle, otData.senderSeed, otData.senderSeedHash);
    //     return otData;
    // }

    public static OtData step3ReceiverInitMatrix(long receiverHandle, OtData otData, long choiceCount) {
        otData.receiverSeed = new long[2];
        otData.receiverMatrix = new long[4*2*(int)choiceCount];
        step3ReceiverSetSeedPack(receiverHandle, otData.receiverPack);
        step5ReceiverInitMatrix(receiverHandle, otData.senderSeed, otData.senderSeedHash, otData.receiverSeed, otData.receiverMatrix);
        return otData;
    }

    public static OtData step4SenderSetMatrix(long senderHandle, OtData otData, long choiceCount, long msgCount) {
        otData.senderMatrix = new long[2*(int)choiceCount*(int)msgCount];
        step6SenderSetMatrix(senderHandle, otData.receiverSeed, otData.receiverMatrix, otData.senderSeed, otData.senderMatrix);
        return otData;
    }

    public static OtData step5ReceiverGetFinalResultWithDecMessage(long receiverHandle, OtData otData, long choiceCount) {
        otData.data = new String[(int)choiceCount];
        step7ReceiverGetFinalResultWithDecMessage(receiverHandle, otData.senderMatrix, otData.enMessage, otData.hash, otData.data);
        return otData;
    }

    public static void main(String[] args) {
        // NativeInterface kkrt = new NativeInterface();
        // String[] s1 = {"aa", "bb", "cc", "dd"};
        // long[] s2 = {100001, 100002, 100003, 10004};

        long choiceCount = 2;
        long msgCount = 500;
        String messages[] = new String[(int)msgCount];
        long keys[] = new long[(int)msgCount];
        for(int i = 0; i < msgCount; i++) {
            messages[i] = "wedpr test message index " + i;
            keys[i] = 10000 + i;
        }
    //     // byte[] s2 = {1, 2};
        long senderHandle = newSender(choiceCount, msgCount, messages, keys);
        System.out.println("senderHandle = "+ senderHandle);
        long[] choice = new long[2];
        for(int i = 0; i < choiceCount; i++) {
            choice[i] = 10050 + i;
        }
        long receiverHandle = newReceiver(choiceCount, msgCount, choice);
        System.out.println("receiverHandle = "+ receiverHandle);
        byte[] senderPackSeed = new byte[49];
        long[] receiverSeed = new long[2];
        step1ReceiverInitBaseOt(receiverHandle, senderPackSeed, receiverSeed);
    //     // 0~48
        // for(int i = 0; i < senderPackSeed.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderPackSeed = "+ senderPackSeed[i]);
        // }
        byte[] receiverPack = new byte[16896];
        step2SenderExtendSeedPack(senderHandle, senderPackSeed, receiverSeed, receiverPack);
        // for(int i = 0; i < receiverPack.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("receiverPack = "+ receiverPack[i]);
        // }
        step3ReceiverSetSeedPack(receiverHandle, receiverPack);

        long[] senderSeed = new long[2];
        byte[] senderSeedHash = new byte[20];
        step4SenderGenerateSeed(senderHandle, senderSeed, senderSeedHash);
        // for(int i = 0; i < senderSeed.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderSeed = "+ senderSeed[i]);
        // }
        // for(int i = 0; i < senderSeedHash.length; i++) {
        //     System.out.println("i = "+ i);
        //     System.out.println("senderSeedHash = "+ senderSeedHash[i]);
        // }

        long[] receiverSeedKkrt = new long[2];
        long[] receiverMatrix = new long[4*2*(int)choiceCount];
        step5ReceiverInitMatrix(receiverHandle, senderSeed, senderSeedHash, receiverSeedKkrt, receiverMatrix);
        // for(int i = 0; i < receiverMatrix.length; i++) {
        //     System.out.println("receiverMatrix = "+ receiverMatrix[i]);
        // }
        long[] senderMatrix = new long[2*(int)choiceCount*(int)msgCount];
        step6SenderSetMatrix(senderHandle, receiverSeedKkrt, receiverMatrix, senderSeed, senderMatrix);
        // for(int i = 0; i < senderMatrix.length; i++) {
        //     System.out.println("senderMatrix = "+ senderMatrix[i]);
        // }
        long[][] enMessage = new long[(int)msgCount][32];
        byte[][] hash = new byte[(int)msgCount][20];
        getSenderEncMessage(senderHandle, enMessage, hash);
        // for(int i = 0; i < enMessage.length; i++) {
        //     System.out.println("enMessage = "+ enMessage[i]);
        // }
        String[] data = new String[2];
        step7ReceiverGetFinalResultWithDecMessage(receiverHandle, senderMatrix, enMessage, hash, data);
        for(int i = 0; i < data.length; i++) {
            System.out.println("data = "+ data[i]);
        }

    }
}
