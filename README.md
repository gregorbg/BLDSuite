# BLDSuite
A collection of useful Java code related to BLD solving

## Setup
There is no actual setup involved because this is just some code.
You will however need to download several libraries to get this project running

### Dependencies
- [MySQL connector/J](https://dev.mysql.com/downloads/connector/j/)
- [AlgLib](https://github.com/suushiemaniac/AlgLib)
- [suushieJSON](https://github.com/suushiemaniac/suushieJSON)
- TNoodle `scrambles` module (via <https://github.com/thewca/tnoodle>)

A local MySQL server setup for database testing is recommended (although not necessary)

## Modules
The entire suite consists of smaller sub-units, so called "modules".
Every module has its own functionality, for which the base workings will be explained below.

The plan is to have all small sub-units integrate as `gradle` or `maven` module at some point in the future.

GUIs will also be written and published here once available!

### model
Contains mostly enums to facilitate work with cube-related properties.
Most importantly, this contains the `piece` package which is heavily used throughout the entire suite.

Algorithms for the WCA-official BLD events can all be accessed via the `CubicPieceType.*` enum constants

### database
A simple backend for managing persistent algorithm storage. Currently needs a custom DDL to be set up:
```mysql
CREATE TABLE Algorithms(
	type VARCHAR(255),
	`case` VARCHAR(2),
	alg VARCHAR(255),
	score FLOAT DEFAULT 0,
	review BOOLEAN DEFAULT FALSE,
	buffer VARCHAR(255),
	UNIQUE KEY (type, `case`, alg, buffer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
```mysql
CREATE TABLE Images(
	`case` VARCHAR(2),
	image VARCHAR(255),
	score INT DEFAULT 0,
	token VARCHAR(255),
	`language` VARCHAR(255),
	UNIQUE KEY (`case`, image, token, `language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### analyze
A package for, well, analyzing a given scramble.

Useful for generating memos, evaluating scrambles and reconstructing solves.
Can even be provided with an `AlgSource` (currently only implemented in the `database` module)
to print full algorithm solutions.

Abstract base implementation is defined in `BldPuzzle.java`, which should be used as static variable type.
Concrete implementations are written in the classes `ThreeBldCube.java`, `FourBldCube.java`, `FiveBldCube.java` and so on.

### filter
A package for conditional BLD scrambling.

Uses an underlying TNoodle implementation to "brute force" a desired scramble.
Please note that depending on your wishes, this can take a very (very, very) long time to yield results.

The abstract base implementation is given in `BldScramble.java`.
Concrete implementations can be found in `ThreeBldScramble.java`, `FourBldScramble.java`, `FiveBldScramble.java` and so on.

The `condition` sub-package contains the required filtering mechanisms.
The following static constructors are available:

`IntCondition.java`:
- `IntCondition EXACT(int n)` specifies exactly `n`
- `IntCondition INTERVAL(int min, int max)` specifies a range between `min` and `max`, inclusive
- `IntCondition MIN(int min)` specifies at least `min`
- `IntCondition MAX(int max)` specifies at most `max`
- `IntCondition ANY()` specifies any amount (inside the Java 32bit `Integer` domain)
- `IntCondition NONE()` specifies no match

`BooleanCondition.java`:
- `BooleanCondition YES()` specifies a condition must apply
- `BooleanCondition NO()` specifies a condition must **not** apply
- `BooleanCondition MAYBE()` specifies a condition *may* apply but it essentially doesn't matter

Additional filters can be specified via [regular expressions](http://www.regular-expressions.info/), namely:
- `setMemoRegex(PieceType type, String regExp)` requires that the given piece type memo matches the given RegExp.
No boundary conditions apply, so if you enter silly stuff it might not work
- `setLetterPairRegex(PieceType type, List<String> pairs)` requires that the given piece type memo contains **all** of the letter pairs
specified in the list passed as second argument
- `setPredicateRegex(PieceType type, AlgSource algSource, Predicate<Algorithm> filter)` requires that the given piece type execution
matches the given predicate from start to end, using the algorithms present in the given source