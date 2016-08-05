package org.apache.cmueller.sample.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.bind.JAXB;

import org.apache.cmueller.sample.model.EBook;

@Stateless
@WebService(endpointInterface = "org.apache.cmueller.sample.service.EBookStore",
    targetNamespace = "http://endpoint.jaxws.javaee7.org/",
    name = "EBookStoreImpl")
public class EBookService implements EBookStore {

    @Override
    public EBook takeBook(String isbn) throws TakeBookFault {
        InputStream is = getClass().getResourceAsStream(String.format("/takeBook_%s_exception.txt", isbn));
        if (is != null) {
            try {
                throw new TakeBookFault(inputStreamAsString(is));
            } catch (Exception e) {
                throw new TakeBookFault(e.getMessage());
            }
        }
        
        is = getClass().getResourceAsStream(String.format("/takeBook_%s_timeout.txt", isbn));
        if (is != null) {
            try {
                Thread.sleep(inputStreamAsLong(is));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        
        is = getClass().getResourceAsStream(String.format("/takeBook_%s.xml", isbn));
        if (is == null) {
            // take the default which is shipped with the mock
            is = getClass().getResourceAsStream("/takeBook_default.xml");
        }
        
        return JAXB.unmarshal(is, EBook.class);
    }
    
    private String inputStreamAsString(InputStream is) throws UnsupportedEncodingException, IOException {
        return new BufferedReader(new InputStreamReader(is, "UTF-8")).readLine();
    }
    
    private long inputStreamAsLong(InputStream is) throws UnsupportedEncodingException, IOException {
        return Long.parseLong(inputStreamAsString(is));
    }
}