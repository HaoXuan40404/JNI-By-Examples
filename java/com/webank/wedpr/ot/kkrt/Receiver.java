package com.webank.wedpr.ot.kkrt;

public class Receiver {
    long receiverHandle;
    long choiceCount;
    long msgCount;

    public void init(long _choiceCount, long _msgCount, long[] choice) {
        choiceCount = _choiceCount;
        msgCount = _msgCount;
        receiverHandle = NativeInterface.initReceiver(choiceCount, msgCount, choice);
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
        OtData otData = NativeInterface.step3ReceiverInitMatrix(receiverHandle, otDataInput, choiceCount);
        return otData;
    }

    public OtData step5GetResult(OtData otDataInput) {
        OtData otData = NativeInterface.step5ReceiverGetFinalResultWithDecMessage(receiverHandle, otDataInput, choiceCount);
        return otData;
    }

}
