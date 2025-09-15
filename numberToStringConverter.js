/**
 * Converts an array of numbers to an array of strings
 * @param {number[]} numberList - Array of numbers to convert
 * @returns {string[]} Array of strings converted from the input numbers
 * @throws {Error} If input is not an array or contains non-numeric values
 */
function convertNumbersToStrings(numberList) {
    // Input validation
    if (!Array.isArray(numberList)) {
        throw new Error('Input must be an array');
    }
    
    // Check if all elements are numbers
    for (let i = 0; i < numberList.length; i++) {
        if (typeof numberList[i] !== 'number' || isNaN(numberList[i])) {
            throw new Error(`Element at index ${i} is not a valid number: ${numberList[i]}`);
        }
    }
    
    // Convert numbers to strings
    return numberList.map(num => String(num));
}

// Export for Node.js environments
if (typeof module !== 'undefined' && module.exports) {
    module.exports = convertNumbersToStrings;
}

// Examples of usage (can be removed in production)
if (typeof window === 'undefined') {
    // Node.js environment - run examples
    console.log('Examples:');
    console.log('convertNumbersToStrings([1, 2, 3]):', convertNumbersToStrings([1, 2, 3]));
    console.log('convertNumbersToStrings([0, -5, 3.14]):', convertNumbersToStrings([0, -5, 3.14]));
    console.log('convertNumbersToStrings([]):', convertNumbersToStrings([]));
}