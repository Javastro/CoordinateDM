package org.ivoa.dm.stc.coords;
/*
 * Created on 25/04/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ivoa.vodml.VodmlModel;
import org.ivoa.vodml.validation.AbstractBaseValidation;
import org.ivoa.vodml.validation.ModelValidator;
import org.junit.jupiter.api.Assertions;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The base class for testing...
 *
 */
public abstract class BaseTests extends AbstractBaseValidation {

   private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
         .getLogger(BaseTests.class);

   protected void validate(CoordsModel modelInstance) throws JAXBException {
      ModelValidator.ValidationResult res = validateModel(modelInstance);
      if(!res.isOk){
         res.printValidationErrors(System.out);
      }
      Assertions.assertTrue(res.isOk,"model content is not valid");

   }

   public <T extends VodmlModel<T>> T modelRoundTripXMLwithTest(T model) throws ParserConfigurationException, JAXBException, TransformerFactoryConfigurationError, TransformerException
   {
      RoundTripResult<T> result = roundtripXML(model.management());
      assertTrue(result.isValid, "reading xml back had errors");
      assertNotNull(result.retval,"returned object from XML serialization null");
      return result.retval;
   }

   public <T extends VodmlModel<T>> T modelRoundTripJSONwithTest(T model) throws JsonProcessingException
   {
      RoundTripResult<T> result = roundTripJSON(model.management());
      assertTrue(result.isValid, "reading JSON back had errors");
      assertNotNull(result.retval,"returned object from JSON serialization null");
      return result.retval;
   }

}
