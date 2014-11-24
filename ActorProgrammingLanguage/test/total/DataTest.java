package total;

import org.junit.Test;

import type.APNumber;

public class DataTest {

    @Test
    public void testDeclarationWith2Params() {
        ProgramTest.testNoError("datatype Rectangle = {width, height};");

    }
    
    @Test
    public void testDeclarationWith2Constructors() {
        ProgramTest
                .testNoError("datatype Rectangle = {width, height} | {size};");

    }

    @Test
    public void testCreationWithTwoConstructors() {
        ProgramTest
        .testNoError("datatype Rectangle = {width, height}|{size};\n"
                + "b = new Rectangle(width=5,height=6);"
                + "c = new Rectangle(size = 7);");
    }

    @Test
    public void testCreationWithTwoParams() {
        ProgramTest.testNoError("datatype Rectangle = {width, height};\n"
                + "b = new Rectangle(width=5,height=6);");
    }
    
    @Test
    public void testAccessOfFieldTwoConstructors() {
        ProgramTest.test("datatype Rectangle = {width, height} | {size}; "
                + "b = new Rectangle(size = 5); a = b.size;",
                new APNumber("5"), "a");
    }
    
    @Test
    public void testAccessOfField() {
        ProgramTest.test("datatype Rectangle = {width, height}; "
                + "b = new Rectangle(width=5,height=6); a = b.width;",
                new APNumber("5"), "a");
    }
    
    @Test
    public void testFunctionAcceptDataType() {
        ProgramTest.test("datatype Rectangle = {width, height}; "
                + "b = new Rectangle(width=5,height=6); "
                + "getWidth = func rect -> rect.width; a = getWidth(b);",
                new APNumber("5"), "a");
    }
    
    @Test
    public void testEditField() {
        ProgramTest.test("datatype Rectangle = {width, height}; "
                + "b = new Rectangle(width=5,height=6); "
                + "b.width = 7; a = b.width;", new APNumber("7"), "a");
    }

    @Test
    public void testAccessAndEditField() {
        ProgramTest
        .test("datatype Rectangle = {width, height}; "
                + "b = new Rectangle(width=5,height=6); "
                + "b.width = b.width + 2; a = b.width;", new APNumber(
                        "7"), "a");
    }

}
