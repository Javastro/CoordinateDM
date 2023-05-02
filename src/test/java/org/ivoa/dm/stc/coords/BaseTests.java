package org.ivoa.dm.stc.coords;
/*
 * Created on 25/04/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ivoa.vodml.ModelManagement;
import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;
import org.javastro.ivoa.tests.AbstractJAXBJPATest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The base class for testing...
 */
public abstract class BaseTests extends AbstractJAXBJPATest  {


   private static JAXBContext jc;
   private static ObjectMapper jsonMapper;

   static {
      try {
         jc = CoordsModel.contextFactory();
         jsonMapper = CoordsModel.jsonMapper();
      } catch (JAXBException e) {
         throw new RuntimeException(e);
      }
   }



   <T> void validate (T p) throws JAXBException {


        @SuppressWarnings("unchecked")
        JaxbAnnotationMeta<T> meta = JaxbAnnotationMeta.of((Class<T>)p.getClass());
        DescriptionValidator<T> validator = new DescriptionValidator<>(jc, meta);
        DescriptionValidator.Validation validation = validator.validate(p);
        if(!validation.valid) {
            System.err.println(validation.message);
        }
        assertTrue(validation.valid);
   }

   <T> T ObroundTripXML(T o) throws JAXBException, ParserConfigurationException, TransformerException {
      @SuppressWarnings("unchecked")
      Class<T> clazz =  (Class<T>)o.getClass();
      return super.roundtripXML(jc,o,clazz);
   }

   protected  <T> T roundTripJSON(T o) throws JsonProcessingException {
      String json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
      System.out.println("JSON output");
      System.out.println(json);
      @SuppressWarnings("unchecked")
      T retval = jsonMapper.readValue(json, (Class<? extends T>) o.getClass());
      assertNotNull(retval);
      return retval;

   }
}
