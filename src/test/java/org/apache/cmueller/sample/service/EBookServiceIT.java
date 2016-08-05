package org.apache.cmueller.sample.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.cmueller.sample.model.EBook;
import org.junit.BeforeClass;
import org.junit.Test;

public class EBookServiceIT {

    private static EBookStore eBookStore;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        EBookStoreImplService service = new EBookStoreImplService(
            new URL("http://localhost:8080/jaxws-mock-sample/EBookStoreImpl?wsdl"),
            new QName("http://endpoint.jaxws.javaee7.org/", "EBookServiceService"));
        
        eBookStore = service.getEBookStoreImplPort();
    }

    @Test
    public void takeBook1() throws TakeBookFault {
        EBook book = eBookStore.takeBook("1");

        assertEquals("1", book.getIsbn());
        assertEquals("Title 1", book.getTitle());
        assertEquals(1, book.getNumPages());
        assertEquals(19.99, book.getPrice(), 0);
    }

    @Test
    public void takeBook2() throws TakeBookFault {
        EBook book = eBookStore.takeBook("2");
        
        assertEquals("2", book.getIsbn());
        assertEquals("Title 2", book.getTitle());
        assertEquals(2, book.getNumPages());
        assertEquals(29.99, book.getPrice(), 0);
    }

    @Test
    public void takeBookDefault() throws TakeBookFault {
        EBook book = eBookStore.takeBook("3");

        assertEquals("0", book.getIsbn());
        assertEquals("Title default", book.getTitle());
        assertEquals(0, book.getNumPages());
        assertEquals(1.99, book.getPrice(), 0);
    }

    @Test
    public void takeBookFault() throws TakeBookFault {
        try {
            eBookStore.takeBook("99");
            fail("TakeBookFault expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("EBook 99 doesn't exist!"));
        }
    }
    
    @Test
    public void takeBookTimeout() throws TakeBookFault, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    eBookStore.takeBook("77");
                    latch.countDown();
                } catch (TakeBookFault e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        
        // we expect a timeout
        assertEquals(false, latch.await(2, TimeUnit.SECONDS));
    }
}