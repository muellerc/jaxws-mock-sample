package org.apache.cmueller.sample.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.cmueller.sample.model.EBook;
import org.junit.Before;
import org.junit.Test;

public class EBookServiceTest {

    private EBookService service;

    @Before
    public void setUp() throws Exception {
        service = new EBookService();
    }

    @Test
    public void takeBook1() throws TakeBookFault {
        EBook book = service.takeBook("1");
        
        assertEquals("1", book.getIsbn());
        assertEquals("Title 1", book.getTitle());
        assertEquals(1, book.getNumPages());
        assertEquals(19.99, book.getPrice(), 0);
    }

    @Test
    public void takeBook2() throws TakeBookFault {
        EBook book = service.takeBook("2");
        
        assertEquals("2", book.getIsbn());
        assertEquals("Title 2", book.getTitle());
        assertEquals(2, book.getNumPages());
        assertEquals(29.99, book.getPrice(), 0);
    }
    
    @Test
    public void takeBookDefault() throws TakeBookFault {
        EBook book = service.takeBook("3");
        
        assertEquals("0", book.getIsbn());
        assertEquals("Title default", book.getTitle());
        assertEquals(0, book.getNumPages());
        assertEquals(1.99, book.getPrice(), 0);
    }
    
    @Test
    public void takeBookFault() throws TakeBookFault {
        try {
            service.takeBook("99");
            fail("TakeBookFault expected");
        } catch (TakeBookFault e) {
            assertTrue(e.getMessage().contains("EBook 99 doesn't exist!"));
        }
    }
    
    @Test
    public void takeBookTimeout() throws TakeBookFault, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    service.takeBook("77");
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