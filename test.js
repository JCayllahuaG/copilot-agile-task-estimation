// Simple test suite for numberToStringConverter
const convertNumbersToStrings = require('./numberToStringConverter.js');

function runTests() {
    console.log('Running tests for convertNumbersToStrings function...\n');
    
    let passedTests = 0;
    let totalTests = 0;
    
    function test(description, testFn) {
        totalTests++;
        try {
            testFn();
            console.log(`✅ PASS: ${description}`);
            passedTests++;
        } catch (error) {
            console.log(`❌ FAIL: ${description}`);
            console.log(`   Error: ${error.message}\n`);
        }
    }
    
    function assertEquals(actual, expected, message = '') {
        if (JSON.stringify(actual) !== JSON.stringify(expected)) {
            throw new Error(`Expected ${JSON.stringify(expected)}, but got ${JSON.stringify(actual)}. ${message}`);
        }
    }
    
    function assertThrows(fn, expectedMessage = '') {
        try {
            fn();
            throw new Error('Expected function to throw an error, but it did not');
        } catch (error) {
            if (expectedMessage && !error.message.includes(expectedMessage)) {
                throw new Error(`Expected error message to contain "${expectedMessage}", but got "${error.message}"`);
            }
        }
    }
    
    // Test cases
    test('converts basic number array to string array', () => {
        const result = convertNumbersToStrings([1, 2, 3]);
        assertEquals(result, ['1', '2', '3']);
    });
    
    test('handles empty array', () => {
        const result = convertNumbersToStrings([]);
        assertEquals(result, []);
    });
    
    test('handles negative numbers', () => {
        const result = convertNumbersToStrings([-1, -2, -3]);
        assertEquals(result, ['-1', '-2', '-3']);
    });
    
    test('handles decimal numbers', () => {
        const result = convertNumbersToStrings([1.5, 2.7, 3.14]);
        assertEquals(result, ['1.5', '2.7', '3.14']);
    });
    
    test('handles zero', () => {
        const result = convertNumbersToStrings([0]);
        assertEquals(result, ['0']);
    });
    
    test('handles mixed positive and negative numbers', () => {
        const result = convertNumbersToStrings([-5, 0, 5, 10.5]);
        assertEquals(result, ['-5', '0', '5', '10.5']);
    });
    
    test('throws error for non-array input', () => {
        assertThrows(() => convertNumbersToStrings("not an array"), 'Input must be an array');
    });
    
    test('throws error for array containing non-numbers', () => {
        assertThrows(() => convertNumbersToStrings([1, 'string', 3]), 'not a valid number');
    });
    
    test('throws error for array containing null', () => {
        assertThrows(() => convertNumbersToStrings([1, null, 3]), 'not a valid number');
    });
    
    test('throws error for array containing undefined', () => {
        assertThrows(() => convertNumbersToStrings([1, undefined, 3]), 'not a valid number');
    });
    
    // Summary
    console.log(`\nTest Results: ${passedTests}/${totalTests} tests passed`);
    
    if (passedTests === totalTests) {
        console.log('🎉 All tests passed!');
        return true;
    } else {
        console.log('❌ Some tests failed');
        return false;
    }
}

// Run tests if this file is executed directly
if (require.main === module) {
    runTests();
}

module.exports = runTests;