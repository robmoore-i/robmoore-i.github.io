# Reader Experience

The importance of readability in your code is broadly accepted to be one of the keys to maintainability and developer happiness. I think there is something to this, but the focus on "readability" is not quite right. When you think about the next person to arrive at your code, as you should, I recommend that you don't think about your code's inherent readability. Rather, think about a person, arriving at the scene of your code:

- What do they know? What might they not be aware of?
- How experienced are they in the technology you're using?
- Are they looking for something?
- Which team are they in?
- Why are they here?

I think focusing on future readers and authors of your code, makes you more effective and sensitive in your efforts to improve maintainability and productivity through refactoring.

Thinking about "readability" centers the code, and puts you at risk of losing the reader. Thinking about "reader experience" centers the reader.

Part of thinking about your reader's experience, is thinking about what readers are trying to achieve when they arrive at your code. Are they just trying to understand, or are they also trying to edit, simplify, delete or move? You can optimize separately for those different experiences, depending on the usage patterns you anticipate for the code you're writing.

When thinking about experiences related to changing the code, as opposed to just reading it, I use the term "author experience".

I think the absolute best way to achieve a high quality reader and author experience, is to [write great tests](/writing/tests).

---
Created on 2022-06-06

Updated on 2022-08-28