package org.monstercraft.irc.plugin.handles.thread;

import org.monstercraft.irc.plugin.Configuration.Variables;
import org.monstercraft.irc.plugin.handles.IRCHandler;

public class OutputThread extends Thread implements Runnable {

    private final IRCHandler parent;

    public OutputThread(final IRCHandler parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        while (parent.isConnected()) {
            try {
                int i = 0;
                while (!parent.getQueue().isEmpty()) {
                    try {
                        final String message = parent.getQueue().remove();
                        parent.write(message + "\r\n");
                        i++;
                        if (i >= Variables.limit) {
                            break;
                        }
                        if (parent.getQueue().isEmpty()) {
                            break;
                        }
                    } catch (final Exception e) {
                        break;
                    }
                }
                if (Variables.limit != 0) {
                    Thread.sleep(1000 / Variables.limit);
                }
            } catch (final Exception e) {
                break;
            }
        }
    }

}
