# SplatCompiler
 
scanner + parser for a fictional functional programming language

## What it does

One example: 
```
def a = 42
let a = 10, b = a + 20 { 
    let a = 20, b = a + b {
      a * b
    }
}
a
```

returns

```
42
1440
42
```


## Running the Parser

### Linux:

Put code that you want to parse in a text file, then run this command:

```./runme.sh < input.txt```
