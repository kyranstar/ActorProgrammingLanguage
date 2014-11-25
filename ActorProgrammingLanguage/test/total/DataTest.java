package total;

import org.junit.Test;

import type.APNumber;

public class DataTest {
    
    @Test
    public void testDeclarationWith2Params() {
        ProgramTest.testNoError("datatype Rectangle = Rect {width, height};");
        
    }

    @Test
    public void testDeclarationWith2Constructors() {
        ProgramTest
        .testNoError("datatype Rectangle = Rect {width, height} | Square {size};");
        
    }
    
    @Test
    public void testCreationWithTwoConstructors() {
        ProgramTest
                .testNoError("datatype Rectangle = Rect {width, height}| Square {size};\n"
                        + "b = new Rectangle.Rect(width=5,height=6);"
                        + "c = new Rectangle.Square(size = 7);");
    }
    
    @Test
    public void testCreationWithTwoParams() {
        ProgramTest.testNoError("datatype Rectangle = Rect {width, height};\n"
                + "b = new Rectangle.Rect(width=5,height=6);");
    }

    @Test
    public void testAccessOfFieldTwoConstructors() {
        ProgramTest.test(
                "datatype Rectangle = Rect {width, height} | Square {size}; "
                        + "b = new Rectangle.Square(size = 5); a = b.size;",
                new APNumber("5"), "a");
    }

    @Test
    public void testAccessOfField() {
        ProgramTest.test("datatype Rectangle = Rect {width, height}; "
                + "b = new Rectangle.Rect(width=5,height=6); a = b.width;",
                new APNumber("5"), "a");
    }

    @Test
    public void testFunctionAcceptDataType() {
        ProgramTest.test("datatype Rectangle = Rect {width, height}; "
                + "b = new Rectangle.Rect(width=5,height=6); "
                + "getWidth = func rect -> rect.width; a = getWidth(b);",
                new APNumber("5"), "a");
    }

    @Test
    public void testEditField() {
        ProgramTest.test("datatype Rectangle = Rect {width, height}; "
                + "b = new Rectangle.Rect(width=5,height=6); "
                + "b.width = 7; a = b.width;", new APNumber("7"), "a");
    }
    
    @Test
    public void testAccessAndEditField() {
        ProgramTest
                .test("datatype Rectangle = Rect {width, height}; "
                        + "b = new Rectangle.Rect(width=5,height=6); "
                        + "b.width = b.width + 2; a = b.width;", new APNumber(
                        "7"), "a");
    }
    @Test
    public void testDeclarationNoArgs() {
        ProgramTest
                .testNoError("datatype Rectangle = Rect {width, height} | Default; "
                        + "b = new Rectangle.Default(); ");
    }
}
