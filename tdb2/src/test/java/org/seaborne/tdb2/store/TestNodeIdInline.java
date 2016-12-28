/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package org.seaborne.tdb2.store;

import static org.junit.Assert.*;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.util.NodeFactoryExtra;
import org.junit.Test;
import org.seaborne.tdb2.store.NodeId;

public class TestNodeIdInline
{
    @Test public void nodeId_int_01()
    { test("'1'^^xsd:integer") ; }

    @Test public void nodeId_int_02()
    { test("2") ; }

    @Test public void nodeId_int_03()
    { test("'3'^^xsd:int") ; }

    @Test public void nodeId_int_04()
    { testNoInline("'3'") ; }

    @Test public void nodeId_int_05()
    { test("-1") ; }
    
    @Test public void nodeId_int_06()
    { test("-180") ; }

    @Test public void nodeId_int_07()
    { test("01",  "1") ; }
    
    @Test public void nodeId_int_08()
    { test("+01",  "1") ; }
    
    @Test public void nodeId_int_09()
    // More than Long.MAX_VALUE
    { testNoInline("92233720368547758070") ; }

    // On the edge.
    
    static final long X = 1L<<55 ;        // Just too large 
    static final long Y = -((1L<<55) +1) ;   // Just too small 
    
    @Test public void nodeId_int_10()
    { testNoInline("\""+Long.toString(X)+"\"^^xsd:integer") ; }

    @Test public void nodeId_int_11()
    { 
        Node n = NodeValue.makeInteger(X-1).asNode() ;
        test("\""+Long.toString(X-1)+"\"^^xsd:integer",  n) ; 
    }

    @Test public void nodeId_int_12()
    { testNoInline("\""+Long.toString(Y)+"\"^^xsd:integer") ; }

    @Test public void nodeId_int_13()
    { 
        Node n = NodeValue.makeInteger(Y+1).asNode() ;
        test("\""+Long.toString(Y+1)+"\"^^xsd:integer",  n) ; 
    }

    @Test public void nodeId_int_20()
    { test("'30'^^xsd:positiveInteger") ; }
    
    @Test public void nodeId_int_21()
    { testNoInline("'300'^^xsd:byte") ; }

    @Test public void nodeId_decimal_1()
    { test("3.14", "3.14") ; }

    @Test public void nodeId_decimal_2()
    { testNoInline("123456789.123456789") ; }
    
    // Just this once, directly create the Node.
    @Test public void nodeId_decimal_3()
    { test("12.89", NodeFactory.createLiteral("12.89", XSDDatatype.XSDdecimal)) ; }

    @Test public void nodeId_decimal_4()
    { test("-1.0",  "-1.0") ; }
    
    // This number has > 47 bits of value : 2412.80478192688
    @Test public void nodeId_decimal_5()
    { testNoInline("2412.80478192688") ; }
    
    // This number has > 47 bits of value : -2412.80478192688
    @Test public void nodeId_decimal_6()
    { testNoInline("-2412.80478192688") ; }

    @Test public void nodeId_decimal_7()
    { test("'0.00000001'^^xsd:decimal",  
           NodeFactory.createLiteral("0.00000001", XSDDatatype.XSDdecimal)) ; 
    }

    @Test public void nodeId_decimal_8()
    { test("0.00000001", NodeFactory.createLiteral("0.00000001", XSDDatatype.XSDdecimal)) ; }

    @Test public void nodeId_dateTime_01()
    { test("'2008-04-28T15:36:15+01:00'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_02()
    { test("'2008-04-28T15:36:15Z'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_03()
    { test("'2008-04-28T15:36:15+00:00'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_04()
    { test("'2008-04-28T15:36:15-05:00'^^xsd:dateTime") ; }

    // No timezone.
    @Test public void nodeId_dateTime_05()
    { test("'2008-04-28T15:36:15'^^xsd:dateTime") ; }

    // Note the trailing zero - system does not preserve perfect lexical forms. 
    @Test public void nodeId_dateTime_06()
    { test("'2008-04-28T15:36:05.450'^^xsd:dateTime", "'2008-04-28T15:36:05.45'^^xsd:dateTime") ; }

    // Java bug: T24:00:00 not accepted by DatatypeFactory.newXMLGregorianCalendar(lex)
//    @Test public void nodeId_dateTime_07()
//    { test("'2008-04-28T24:00:00'^^xsd:dateTime", NodeFactory.parseNode("'2008-04-29T00:00:00'^^xsd:dateTime")) ; }
    
    // Out of range.
    @Test public void nodeId_dateTime_08()
    { testNoInline("'8008-04-28T15:36:05.45'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_09()
    { test("'2008-04-28T15:36:05.001'^^xsd:dateTime") ; }
    
    @Test public void nodeId_dateTime_10()
    { test("'2008-04-28T15:36:05.01'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_11()
    { test("'2008-04-28T15:36:05.1'^^xsd:dateTime") ; }

    // Canonicalization test - fractional seconds.
    @Test public void nodeId_dateTime_12()
    { test("'2008-04-28T15:36:05.010'^^xsd:dateTime", "'2008-04-28T15:36:05.01'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_13()
    { test("'2008-04-28T15:36:05.100'^^xsd:dateTime", "'2008-04-28T15:36:05.1'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_14()
    { test("'2012-07-29T20:39:11.100+01:15'^^xsd:dateTime", "'2012-07-29T20:39:11.1+01:15'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_15()
    { test("'2012-07-29T20:39:11.100-01:15'^^xsd:dateTime", "'2012-07-29T20:39:11.1-01:15'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_16()
    { test("'2012-07-29T20:39:11.100+01:30'^^xsd:dateTime", "'2012-07-29T20:39:11.1+01:30'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_17()
    { test("'2012-07-29T20:39:11.100-01:45'^^xsd:dateTime", "'2012-07-29T20:39:11.1-01:45'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTime_18()
    { test("'2012-07-29T20:39:11.100Z'^^xsd:dateTime", "'2012-07-29T20:39:11.1Z'^^xsd:dateTime") ; }

    @Test public void nodeId_dateTimeStamp_01()
    { test("'2012-07-29T20:39:11.100Z'^^xsd:dateTimeStamp", "'2012-07-29T20:39:11.1Z'^^xsd:dateTimeStamp") ; }

    
    @Test public void nodeId_date_1()
    { test("'2008-04-28Z'^^xsd:date", "'2008-04-28Z'^^xsd:date") ; }

    @Test public void nodeId_date_2()
    { test("'2008-04-28+00:00'^^xsd:date", "'2008-04-28+00:00'^^xsd:date") ; }

    @Test public void nodeId_date_3()
    { test("'2008-04-28-05:00'^^xsd:date", "'2008-04-28-05:00'^^xsd:date") ; }

    @Test public void nodeId_date_4()
    { test("'2008-04-28+02:00'^^xsd:date", "'2008-04-28+02:00'^^xsd:date") ; }

    @Test public void nodeId_date_5()
    { testNoInline("'8008-04-28'^^xsd:date") ; }

    @Test public void nodeId_date_6()
    { test("'2012-07-29+06:15'^^xsd:date", "'2012-07-29+06:15'^^xsd:date") ; }

    @Test public void nodeId_date_7()
    { test("'2012-07-29-06:30'^^xsd:date", "'2012-07-29-06:30'^^xsd:date") ; }

    @Test public void nodeId_boolean_1()
    { test("'true'^^xsd:boolean", "'true'^^xsd:boolean") ; }

    @Test public void nodeId_boolean_2()
    { test("'false'^^xsd:boolean", "'false'^^xsd:boolean") ; }

    @Test public void nodeId_boolean_3()
    { test("'1'^^xsd:boolean", "'true'^^xsd:boolean") ; }

    @Test public void nodeId_boolean_4()
    { test("'0'^^xsd:boolean", "'false'^^xsd:boolean") ; }

    @Test public void nodeId_float_1()
    { test("'0.0'^^xsd:float") ; }

    @Test public void nodeId_float_2()
    { test("'0'^^xsd:float", "'0.0'^^xsd:float") ; }
    
    @Test public void nodeId_float_3()
    { testNoInline("'x'^^xsd:float") ; }
    
    @Test public void nodeId_float_4()
    { test("'1.1e1'^^xsd:float", "'11.0'^^xsd:float") ; }

    @Test public void nodeId_float_5()
    { test("'1.1E9'^^xsd:float") ; }

//    @Test public void nodeId_double_1()
//    { test("'0.0'^^xsd:double") ; }
//
//    @Test public void nodeId_double_2()
//    { test("'0'^^xsd:double", "'0.0'^^xsd:double") ; }
//    
//    @Test public void nodeId_double_3()
//    { testNoInline("'x'^^xsd:double") ; }
//    
//    @Test public void nodeId_double_4()
//    { test("'1.1e1'^^xsd:double", "'11.0'^^xsd:double") ; }
//
//    @Test public void nodeId_double_5()
//    { test("'1.1E9'^^xsd:double") ; }
//    
//    @Test public void nodeId_double_6()
//    { test("1.1E9") ; }

    private void test(String x) { test(x, x) ; }
    
    private void test(String x, String expected)
    {
        test(x, NodeFactoryExtra.parseNode(expected)) ;
    }

    private void testNoInline(String x) {
        Node n = NodeFactoryExtra.parseNode(x) ;
        NodeId nodeId = NodeId.inline(n) ;
        assertNull("Converted NodeId but datatype test was false: "+x, nodeId) ;
    }
    
    private void test(String x, Node correct)
    {
        Node n = NodeFactoryExtra.parseNode(x) ;
        NodeId nodeId = NodeId.inline(n) ;
        assertNotNull("Expected inlining: "+x, nodeId);
        
        boolean b = NodeId.hasInlineDatatype(n) ;
        assertTrue("Converted NodeId but datatype test was false", b) ;
        Node n2 = NodeId.extract(nodeId) ;
        assertNotNull("Expected recovery", n2) ;
        String s = "("+correct.getLiteralLexicalForm()+","+n2.getLiteralLexicalForm()+")" ;
        assertTrue("Not same value: "+s, correct.sameValueAs(n2)) ;
        // Term equality.
        assertEquals("Not same term", correct, n2) ;
    }
}
