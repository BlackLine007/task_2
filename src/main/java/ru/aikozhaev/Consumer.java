package ru.aikozhaev;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;


public class Consumer implements Runnable {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

    private MySQLConnection sqlConnection;

    public MySQLConnection getSqlConnection() {
        return sqlConnection;
    }

    public void setSqlConnection(MySQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public Consumer(ConnectionFactory connectionFactory,
                    MySQLConnection sqlConnection) {
        this.connectionFactory = connectionFactory;
        this.sqlConnection = sqlConnection;
    }

    public void connectAndGet(Boolean transactionSupport, String confirmType, String typeOfModel) {
        try {
            // Построить объект подключения с завода
            connection = connectionFactory.createConnection();
            // начало
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
            // Получить соединение операции
            session = connection.createSession(transactionSupport,
                    confirmTypeInteger);
            // очередь тестов соответствует отправителю, каждый создан для получения
            destination = typeOfModel.equals("Queue") ? session.createQueue("test-queue") : session.createTopic("test-topic");
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message msg) {
                    System.out.println("==================");
                    try {
                        System.out.println("Сообщение получил 1-й клиент, текст сообщения: "
                                + ((TextMessage) msg).getText());
                //    sqlConnection.insertIntoDatabase(((TextMessage) msg).getText());
                    sqlConnection.insertIntoDatabaseHeaders(msg.getJMSMessageID(), String.valueOf(msg.getJMSDestination()), msg.getJMSDeliveryMode(),
                        msg.getJMSTimestamp(), msg.getJMSExpiration(), msg.getJMSPriority(), msg.getJMSCorrelationID(),msg.getJMSType(), msg.getJMSRedelivered());

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
           /* MessageConsumer consumer1 = session.createConsumer(destination);
            consumer1.setMessageListener(new MessageListener() {
                public void onMessage(Message msg) {
                    System.out.println("--------------------");
                    try {
                        System.out.println("Сообщение получил 2-й клиент, текст сообщения: "
                                + ((TextMessage) msg).getText());

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    // если у нас CLIENT_ACKNOWLEDGE, то мы должны подтвердить получение сообщений
                    try {
                        if (session.getAcknowledgeMode() == 2) {
                            msg.acknowledge();
                        }
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        // если отправлено несколько сообщений и надо принять их всех, а не только первое
        // то, не закрываем соединение, чтобы получатель смог принять их все, иначе примет только одно (первое)
/*     finally {
            try {
                if (null != connection)
                    connection.close();
            } catch (Throwable ignore) {
            }*/
        }


    @Override
    public void run() {
       /* try {
            Thread.sleep(70000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        connectAndGet(false, "AUTO_ACKNOWLEDGE", "Queue");
    }
}


