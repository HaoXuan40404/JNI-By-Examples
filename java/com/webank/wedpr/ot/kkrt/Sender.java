package com.webank.wedpr.ot.kkrt;

import java.util.HashMap;

public class Sender {
    long senderHandle;
    long choiceCount;
    long msgCount;

    public void init(long _choiceCount, long _msgCount, HashMap<Long, String> data) {
        choiceCount = _choiceCount;
        msgCount = _msgCount;
        senderHandle = NativeInterface.initSender(choiceCount, msgCount, data);
    }

    private OtData getSenderEncMessage() {
        OtData otData = NativeInterface.getSenderEncMessage(senderHandle, msgCount);
        return otData;
    }

    public OtData step2ExtendSeedPack(OtData otDataInput) {
        OtData otData = NativeInterface.step2SenderExtendSeedPack(senderHandle, otDataInput);
        return otData;
    }

    // public OtData step4SenderGenerateSeed(OtData otDataInput) {
    //     OtData otData = NativeInterface.step2SenderExtendSeedPack(senderHandle, otDataInput);
    //     return otData;
    // }

    public OtData step4SetMatrix(OtData otDataInput) {
        OtData otData = NativeInterface.step4SenderSetMatrix(senderHandle, otDataInput, choiceCount, msgCount);
        // set EncMessage and send it to Receiver
        OtData newData = getSenderEncMessage();
        otData.enMessage = newData.enMessage;
        otData.hash = newData.hash;
        return otData;
    }



}
