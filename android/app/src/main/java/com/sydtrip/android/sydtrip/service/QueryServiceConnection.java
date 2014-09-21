package com.sydtrip.android.sydtrip.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.sydtrip.android.sydtrip.model.Query;
import com.sydtrip.android.sydtrip.model.QueryResult;
import com.sydtrip.android.sydtrip.util.EnumUtils;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class QueryServiceConnection implements ServiceConnection {
    private Messenger mService;

    private final Messenger mReplyMessenger;

    public QueryServiceConnection(QueryCallback callback) {
        mReplyMessenger = new Messenger(new IncomingCommandHandler(callback));
    }

    public void bind(Context context) {
        context.bindService(new Intent(context, QueryService.class),
                this, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context) {
        try {
            unregister();
        } finally {
            context.unbindService(this);
        }
    }

    public void query(Query query) {
        final Message msg = getMessage(Command.QUERY);
        msg.obj = query;
        if (!send(msg)) {
            mService = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        mService = new Messenger(service);
        register();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }

    public boolean isConnected() {
        return mService != null;
    }

    private void register() {
        if (!send(getMessage(Command.REGISTER))) {
            mService = null;
        }
    }

    private void unregister() {
        if (!send(getMessage(Command.UNREGISTER))) {
            mService = null;
        }
    }

    private boolean send(Message message) {
        if (message != null) {
            try {
                mService.send(message);
                return true;
            } catch (RemoteException e) {
                Timber.e(e, "Error sending %s message to IBeaconService", message.what);
            }
        }

        return false;
    }

    private Message getMessage(Command command) {
        final Message msg = command.message();
        if (msg != null) {
            msg.replyTo = mReplyMessenger;
        }
        return msg;
    }

    private static class IncomingCommandHandler extends Handler {
        private final WeakReference<QueryCallback> mCallback;

        public IncomingCommandHandler(QueryCallback callback) {
            mCallback = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            final QueryCallback cb = mCallback.get();
            if (cb != null) {
                final Command cmd = EnumUtils.from(Command.class, msg.what);
                if (cmd == null || !cmd.isClientCommand) {
                    Timber.d("Ignoring command: %s", cmd);
                } else {
                    switch (cmd) {
                        case QUERY_RESULT:
                            cb.onQueryResult((QueryResult) msg.obj);
                            break;
                        default:
                            Timber.d("Got command: %s", cmd);
                    }
                }
            }

            super.handleMessage(msg);
        }
    }

    public interface QueryCallback {
        public void onQueryResult(QueryResult result);
    }
}