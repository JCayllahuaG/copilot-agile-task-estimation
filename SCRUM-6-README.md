# Number to String Converter Function

This document describes the JavaScript function created to fulfill SCRUM-6 requirements.

## Function: `convertNumbersToStrings`

Converts an array of numbers to an array of strings.

### Parameters
- `numberList` (number[]): Array of numbers to convert

### Returns
- `string[]`: Array of strings converted from the input numbers

### Throws
- `Error`: If input is not an array or contains non-numeric values

## Usage Examples

### Basic Usage
```javascript
const convertNumbersToStrings = require('./numberToStringConverter.js');

// Convert basic numbers
const result1 = convertNumbersToStrings([1, 2, 3]);
console.log(result1); // ['1', '2', '3']

// Handle negative numbers and decimals
const result2 = convertNumbersToStrings([-5, 0, 3.14, 42]);
console.log(result2); // ['-5', '0', '3.14', '42']

// Empty array
const result3 = convertNumbersToStrings([]);
console.log(result3); // []
```

### Browser Usage
```html
<script src="numberToStringConverter.js"></script>
<script>
    const numbers = [10, 20, 30];
    const strings = convertNumbersToStrings(numbers);
    console.log(strings); // ['10', '20', '30']
</script>
```

### Node.js Usage
```javascript
const convertNumbersToStrings = require('./numberToStringConverter.js');
const result = convertNumbersToStrings([1, 2, 3]);
```

## Error Handling

The function validates input and throws descriptive errors:

```javascript
// Non-array input
convertNumbersToStrings("not an array"); // Error: Input must be an array

// Array with non-numeric values
convertNumbersToStrings([1, "string", 3]); // Error: Element at index 1 is not a valid number: string

// Array with null/undefined
convertNumbersToStrings([1, null, 3]); // Error: Element at index 1 is not a valid number: null
```

## Testing

Run the test suite to verify the function works correctly:

```bash
node test.js
```

The test suite includes 10 comprehensive test cases covering:
- Basic number-to-string conversion
- Empty arrays
- Negative numbers
- Decimal numbers
- Mixed number types
- Error conditions for invalid inputs

## Files

- `numberToStringConverter.js` - Main function implementation
- `test.js` - Comprehensive test suite
- `SCRUM-6-README.md` - This documentation file