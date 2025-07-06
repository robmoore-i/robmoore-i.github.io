# Tests

Writing great tests is an important part of providing a wonderful [reader experience and author experience](/writing/reader-experience) to your colleagues.

Here is a rough checklist I made related to writing a great test suite in an object-oriented programming language. I find these principles easy to interpret from the perspective of a non-object-oriented programming language (like Rust or Q) as well.

1. Tests are as fast as possible, given their scope. Tests that have a noticeable total execution time are scrutinized.
2. Tests are deterministic. The test author makes a reasonable effort to preempt and detect flakiness in the tests they write.
3. A test method fails for exactly one reason. That is, there is only one assertion. Tests do not "ramble", with many actions and assertions. Such tests are broken up into multiple tests.
4. A unit test class contains tests both for expected, successful outcomes and any other potential outcomes. The former are more important, because they prove that the unit under test *can ever possibly work*, if nothing goes wrong at all. Later tests, for exceptional or unexpected behaviours are also important, because they help future readers and authors understand exactly how the unit behaves under a diverse range of circumstances.
5. The test author watched the test fail, either because by doing TDD and writing the test before writing the source code that implements the behaviour they are testing, or, by going back and breaking the program's behaviour to make sure the test will actually catch a regression.
6. The test author has honed the test's failure message(s) so that it is easy for others to interpret a failure. The name of the test method, and the name of the test class, also contribute to providing sufficient context, such that the next person to encounter this failing test understands why it failed, what behaviour it is protecting, and how to fix it.
7. Test code is crafted and refactored like production code. Logical entities in the test are extracted into domain-specific classes within the test sources. Duplication is regarded as an opportunity to create meaningful abstractions. Names are deliberated upon, to provide an intuitive reader experience.
8. Tests can be executed locally by developers on their machine, using their own, possibly modified, copy of the code. Use of secret credentials and custom configurations for tests is discouraged, especially if they are present only on CI, and therefore more difficult for new or inexperienced team members to discover.
9. The name of a test should describe a general behaviour that your unit exhibits e.g. "checks that file exists" rather than "throws exception if argument to --file is non-existing file". Don't put the body of the test in the name of the test.
10. Source code is written with testability in mind. Testability is an essential feature of high-quality production code. Code should be deliberately written in a way that makes it easy for you and other authors to write fast-running automated tests which verify that your program behaves correctly.

---
Created on 2022-06-07

Updated on 2024-01-28