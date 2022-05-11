package ru.aikozhaev;

import javax.jms.*;

public class Sender implements Runnable {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    private static final int MESSAGE_COUNT = 5;

    public Sender(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void connectAndTransfer(Boolean transactionSupport, String confirmType, String typeOfModel) {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            int confirmTypeInteger = 0;
            switch (confirmType) {
                case "AUTO_ACKNOWLEDGE":
                    confirmTypeInteger = 1;
                    break;
                case "CLIENT_ACKNOWLEDGE":
                    confirmTypeInteger = 2;
                    break;
                case "DUPS_OK_ACKNOWLEDGE":
                    confirmTypeInteger = 3;
                    break;
                case "SESSION_TRANSACTED":
                    break;
            }
            session = connection.createSession(transactionSupport,
                    confirmTypeInteger);
            destination = typeOfModel.equals("Queue") ? session.createQueue("test-queue") : session.createTopic("test-topic");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            // Построить сообщение
            sendMessage(session, producer);
            //Для снятия всех блокировок и сохранения сообщений, отправленных в сессии, выполняем:
            if (transactionSupport) session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection)
                    connection.close();
            } catch (Throwable ignore) {
            }
        }
    }

    public static void sendMessage(Session session, MessageProducer producer) throws JMSException {
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            TextMessage message = session
                    .createTextMessage("Сообщение № " + i);
            // отправить сообщение по назначению
            System.out.println("Отправлено: Сообщение № " + i);
            producer.send(message);
        }
    }

    @Override
    public void run() {
      /*  try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        connectAndTransfer(false, "AUTO_ACKNOWLEDGE", "Queue");
    }
}

