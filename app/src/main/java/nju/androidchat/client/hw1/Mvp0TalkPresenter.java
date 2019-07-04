package nju.androidchat.client.hw1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp0TalkPresenter implements Mvp0Contract.TalkPresenter {

    private Mvp0Contract.TalkModel mvp0TalkModel;
    private Mvp0Contract.TalkView iMvp0TalkView;

    private List<ClientMessage> clientMessages;

    @Override
    public void sendMessage(String content) {
        ClientMessage clientMessage = mvp0TalkModel.sendInformation(content);
        refreshMessageList(clientMessage);
    }

    @Override
    public void receiveMessage(ClientMessage clientMessage) {
        refreshMessageList(clientMessage);
    }

    @Override
    public String getUsername() {
        return mvp0TalkModel.getUsername();
    }

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp0TalkView.showMessageList(clientMessages);
    }

    //撤回消息，Mvp0暂不实现
    @Override
    public void recallMessage(UUID messageId) {
        // 操作界面
        List<ClientMessage> newMessages = new ArrayList<>();
        for (ClientMessage clientMessage : clientMessages) {
            if (clientMessage.getMessageId().equals(messageId)) {
                newMessages.add(new ClientMessage(clientMessage.getMessageId(), clientMessage.getTime(), clientMessage.getSenderUsername(), "(已撤回)"));
            } else {
                newMessages.add(clientMessage);
            }
        }
        this.clientMessages = newMessages;
        this.iMvp0TalkView.showMessageList(newMessages);

        // 操作数据
        this.mvp0TalkModel.recallMessage(messageId);

    }

    @Override
    public void start() {

    }
}