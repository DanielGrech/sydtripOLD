package com.sydtrip.android.sydtrip.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.sydtrip.android.sydtrip.data.RoutingEngine;
import com.sydtrip.android.sydtrip.query.IQueryEngine;
import com.sydtrip.android.sydtrip.query.Query;
import com.sydtrip.android.sydtrip.query.QueryEngine;
import com.sydtrip.android.sydtrip.util.EnumUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class QueryService extends Service {

    private final Handler mHandler;
    private final Messenger mMessenger;

    private final List<Messenger> mClients;

    private IQueryEngine mQueryEngine;

    public QueryService() {
        mHandler = new IncomingCommandHandler(this);
        mMessenger = new Messenger(mHandler);
        mClients = new LinkedList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mQueryEngine = new QueryEngine(new RoutingEngine(getApplicationContext()));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void onRegister(Message message) {
        if (message.replyTo != null) {
            mClients.add(message.replyTo);
        }
    }

    private void onUnregister(Message message) {
        if (message.replyTo != null) {
            mClients.remove(message.replyTo);
        }
    }

    private void onQuery(Query query) {
        Timber.d("Running query %s", query);

        final Message msg = Command.QUERY_RESULT.message();
        msg.obj = mQueryEngine.execute(query);
        send(msg);
    }

    private void send(Message msg) {
        final Iterator<Messenger> clientIter = mClients.iterator();
        while (clientIter.hasNext()) {
            final Messenger client = clientIter.next();
            if (!send(client, Message.obtain(msg))) {
                clientIter.remove();
            }
        }
    }

    private boolean send(Messenger client, Message message) {
        if (message != null) {
            try {
                client.send(message);
                return true;
            } catch (RemoteException e) {
                Timber.e(e, "Error sending %s message to client %s", message.what, client);
            }
        }

        return false;
    }

    private static class IncomingCommandHandler extends Handler {
        private final WeakReference<QueryService> mService;

        public IncomingCommandHandler(QueryService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            final QueryService service = mService.get();
            final Command cmd = EnumUtils.from(Command.class, msg.what);
            if (service != null && cmd != null) {
                Timber.d("Got message %s", cmd);
                switch (cmd) {
                    case REGISTER:
                        service.onRegister(msg);
                        break;

                    case UNREGISTER:
                        service.onUnregister(msg);
                        break;

                    case QUERY:
                        service.onQuery((Query) msg.obj);
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }
}
