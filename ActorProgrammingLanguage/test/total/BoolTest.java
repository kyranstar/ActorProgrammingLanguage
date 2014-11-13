package total;

import java.math.BigDecimal;

import org.junit.Test;

public class BoolTest {
    /** The number 10. */
    final BigDecimal expected10 = new BigDecimal("10");

    /** The variable named a. */
    final String variableNameA = "a";
    
    @Test
    public void testIfCondition() {
        ProgramTest.test("a = if true then 10 else 11;", expected10,
                variableNameA);
    }
    
    @Test
    public void testIfNegativeCondition() {
        ProgramTest.test("a = if false then 11 else 10;", expected10,
                variableNameA);
    }
    
    @Test
    public void testLessThan() {
        ProgramTest.test("a = if 3 < 4 then 10 else 11;", expected10,
                variableNameA);
    }
    
    @Test
    public void testGreaterThan() {
        ProgramTest.test("a = if 3 > 4 then 11 else 10;", expected10,
                variableNameA);
    }
    
    @Test
    public void testLessThanEquals() {
        ProgramTest.test("a = if 3 <= 3 then 10 else 11;", expected10,
                variableNameA);
    }
    
    @Test
    public void testGreaterThanEquals() {
        ProgramTest.test("a = if 3 >= 3 then 10 else 11;", expected10,
                variableNameA);
        ProgramTest.test("a = if 3 >= -3 then 10 else 11;", expected10,
                variableNameA);
        ProgramTest.test("a = if 3 >= (-3+5) then 10 else 11;", expected10,
                variableNameA);
    }
    
    @Test
    public void testEquals() {
        ProgramTest.test("a = true = true;", true, variableNameA);
        ProgramTest.test("a = false = false;", true, variableNameA);
        ProgramTest.test("a = false = true;", false, variableNameA);
        ProgramTest.test("a = true = false;", false, variableNameA);
        ProgramTest.test("a = 4 = 4;", true, variableNameA);
        ProgramTest.test("a = 3 = 4;", false, variableNameA);
    }
    
    @Test
    public void testAnd() {
        ProgramTest.test("a = true && true;", true, variableNameA);
        ProgramTest.test("a = true && false;", false, variableNameA);
        ProgramTest.test("a = false && true;", false, variableNameA);
        ProgramTest.test("a = false && false;", false, variableNameA);
    }

    @Test
    public void testOr() {
        ProgramTest.test("a = true || true;", true, variableNameA);
        ProgramTest.test("a = true || false;", true, variableNameA);
        ProgramTest.test("a = false || true;", true, variableNameA);
        ProgramTest.test("a = false || false;", false, variableNameA);
    }
}
