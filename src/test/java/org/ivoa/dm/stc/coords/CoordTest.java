package org.ivoa.dm.stc.coords;
/*
 * Created on 25/04/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.Unit;
import org.junit.jupiter.api.Assertions;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * test cases for STC Coords.
 * they are the same as https://github.com/mcdittmar/ivoa-dm-examples/blob/master/assets/examples/coords/current/instances/
 */
public class CoordTest extends BaseTests {

    @org.junit.jupiter.api.Test
    public  void testAstroCoordSys() throws JAXBException, JsonProcessingException, ParserConfigurationException, TransformerException {
       Unit deg = new Unit("deg");
         SpaceSys ICRS_SYS = new SpaceSys().withFrame(
               SpaceFrame.createSpaceFrame( f-> {
                   f.refPosition = new StdRefLocation("TOPOCENTRE");
                   f.spaceRefFrame="ICRS";
                   f.planetaryEphem="DE432";
                  }
                  ));

         TimeSys TIMESYS_TT = new TimeSys().withFrame(
               TimeFrame.createTimeFrame( f -> {
                  f.refPosition = new StdRefLocation("TOPOCENTRE");
                  f.timescale = "TT";
                  f.refDirection = new CustomRefLocation()
                        .withEpoch(new Epoch("J2014.25"))
                        .withPosition(
                              LonLatPoint.createLonLatPoint(p-> {
                                 p.lon = new RealQuantity(6.752477,deg);
                                 p.lat = new RealQuantity(-16.716116,deg);
                                 p.dist = new RealQuantity(8.6, new Unit("ly"));
                                 p.coordSys = ICRS_SYS;
                                    }
                              )
                        );
               })
         );
         GenericSys SPECSYS = new GenericSys().withFrame(
               GenericFrame.createGenericFrame(f -> {
                  f.refPosition = new StdRefLocation("TOPOCENTRE");
                  f.planetaryEphem = "DE432";
                     }
               )
         );
       CoordsModel modelInstance = new CoordsModel();
       modelInstance.addReference(ICRS_SYS);
       modelInstance.addReference(SPECSYS);
       modelInstance.addReference(TIMESYS_TT);
       modelInstance.makeRefIDsUnique();


       // test model instance validity
       validate(modelInstance);


       //do some serialization tests
       CoordsModel inxml = modelRoundTripXMLwithTest(modelInstance);
       CoordsModel injson = modelRoundTripJSONwithTest(modelInstance);
    }

}