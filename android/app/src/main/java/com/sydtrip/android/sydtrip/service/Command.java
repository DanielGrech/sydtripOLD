package com.sydtrip.android.sydtrip.service;

import android.os.Message;

enum Command {
    REGISTER(false),
    UNREGISTER(false),
    QUERY(false),

    QUERY_RESULT(true);

    final boolean isClientCommand;

    private Command(boolean isClientCommand) {
        this.isClientCommand = isClientCommand;
    }

    public Message message() {
        return Message.obtain(null, ordinal());
    }
}