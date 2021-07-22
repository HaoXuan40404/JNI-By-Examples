package com.webank.wedpr.ot.kkrt;

public class Receiver {
    long receiverHandle;
    int choiceCount;
    int messageCount;

    public void init(int choiceCount, int messageCount, long[] choice) {
        this.choiceCount = choiceCount;
        this.messageCount = messageCount;
        receiverHandle = NativeInterface.initReceiver(choiceCount, messageCount, choice);
    }

    public OtData step1InitBaseOt() {
        OtData otData = NativeInterface.step1ReceiverInitBaseOt(receiverHandle);
        return otData;
    }

    // public OtData step3ReceiverSetSeedPack(OtData otDataInput) {
    //     OtData otData = NativeInterface.step3ReceiverSetSeedPack(receiverHandle, otDataInput);
    //     return otData;
    // }


    public OtData step3InitMatrix(OtData otDataInput) {
        otDataInput = NativeInterface.step3ReceiverInitMatrix(receiverHandle, otDataInput, choiceCount);
        return otDataInput;
    }

    public OtResult step5GetResult(OtData otDataInput) {
        OtResult otResult = NativeInterface.step5ReceiverGetFinalResultWithDecMessage(receiverHandle, otDataInput, choiceCount);
        return otResult;
    }

    public void free() {
        NativeInterface.freeReceiver(receiverHandle);
    }

}
