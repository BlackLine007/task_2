package ru.aikozhaev;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext
                ("applicationContext.xml")) {
            //  MySQLConnection mySQConnection = context.getBean("mySQConnection", MySQLConnection.class);;
            Sender sender = context.getBean("sender", Sender.class);
            thread(sender);
            Consumer consumer = context.getBean("consumer", Consumer.class);
            thread(consumer);
        }
    }
    public static void thread(Runnable runnable) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.start();
    }
}
