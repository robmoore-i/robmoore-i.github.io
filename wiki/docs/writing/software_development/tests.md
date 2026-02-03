# Tests

Writing great tests is an essential part of providing a wonderful [reader experience and author experience](/writing/software_development/reader-experience) to your colleagues.

Here is a kind of manifesto I made about writing a great test suite in an object-oriented programming language. I find these principles easy to interpret also from the perspective of a non-object-oriented programming language (like Rust or Q).

1. Tests are as fast as possible, given their scope. Tests with a noticeable total execution time are scrutinized for their scope and implementation.
2. Tests are deterministic. The test author makes a reasonable effort to foresee and prevent flakiness in the tests they write.
3. A test method fails for exactly one reason. This is usually achieved by having only one assertion. Tests do not ramble with many actions and assertions. Such tests are broken up into smaller, more focused tests.
4. A test class verifies both expected, successful outcomes, and any other potential outcomes. The former are more important, because they prove that the program *can ever possibly work*, if nothing goes wrong at all. Other tests for exceptional or unexpected behaviours are also important, because they help future readers and authors understand exactly how the program behaves under a diverse range of circumstances.
5. The test author watched the test fail. The ideal way to achieve this is by practicing TDD, in which the test author writes and runs the test before implementing the behaviour being tested. Another adequate way to achieve this is by breaking the program's behaviour after writing the test, to make sure the test will actually fail if there is a regression.
6. The test author has honed the test's failure message(s) so that it is easy for others to interpret a failure. The name of the test method and the name of the test class also contribute to providing sufficient context. The next person to encounter this test when it's failing should understand why it's failing, what behaviour it is protecting, and how to fix it.
7. Test code is crafted and refactored like production code. Logical entities in the test are extracted into domain-specific classes within the test sources. Duplication is regarded as an opportunity to create meaningful abstractions. Names are deliberated upon to provide an intuitive reader experience.
8. Tests can be executed locally by developers on their machine, using their own, possibly modified, copy of the code. You should not need to manually set a cluster of environment variables to run a test, especially if their values can only be found by trawling through your CI provider's configuration. Such tests are difficult for anyone to use, but are most problematic for new or inexperienced team members.
9. The test name isn't an abridged form of the test body. The name of a test should describe a general behaviour that your program exhibits e.g. "checks that file exists" rather than "throws exception if argument to --file is non-existing file".
10. Source code is written with testability in mind. Testability is an essential feature of high-quality production code. Code should be deliberately written in a way that makes it easy for you and other authors to write fast-running automated tests which verify that your program behaves correctly.

---
Created on 2022-06-07

Updated on 2026-02-02
