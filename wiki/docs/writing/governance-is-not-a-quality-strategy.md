# WIP: Governance is not a quality strategy

In this page, I rehash Deming's truism:

> Inspection does not improve the quality, nor guarantee quality. Inspection is too late. The quality, good or bad, is already in the product. As Harold F. Dodge said, “You can not inspect quality into a product.”

## Problem: Low quality work

There's a lot of rubbish software out there at the moment. Large, well-funded organisations are often guilty of producing low quality software, which huge numbers of people have to suffer from. In the recent past low quality software has been fatal and ruined many lives. Much thought and money has been injected into coming up with a reproducible process for building high quality software, yet the vast majority of efforts in organisations of all sizes have yielded paltry benefits, if any at all. This is because most organisations rely on false intuition, from which they arrive at the below non-solution.

## Not a solution: Quality gates

Leaders facing quality problems, who are not aware of Deming's truism, use their intuition to tackle the problem. They reason that by adding quality checks and inspections during the process of building software, they will be able to ensure that only high quality software gets deployed and impacts their customers. In my experience, this line of thinking is incredible attractive to people at all levels of experience. It all seems so simple.

There are varying degrees to which different engineering organisations get this wrong, all around the same theme:

- Very wrong: Introduction of a manual testing / approval step.
- Another effort pursuing the same incorrect idea: Mandatory code review for all changes.
- Another effort pursuing the same incorrect idea: All changes must include automated tests and code coverage must be 95%.

The incorrect idea is that adding quality checks improves quality. To paraphrase Deming, Inspection is too late! Whatever level of quality the work is, _it's already done_. Nothing you do after the work has happened can increase its quality. To improve quality, you must improve the work done.

Maybe you believe, quite plausibly I think, that the quality of work every engineer does lies on a normal distribution. Your misstep is in believing that you can eliminate all the work on the lower end the distribution, retaining only the work on the upper end. Unfortunately, software engineering work doesn't slice like that. Producing code is not like producing uniform iron screws, where you can throw out the bad and keep the rest. It's more like producing essays, or music compositions - the work integrates with itself. I won't be able to compose music like Hans Zimmer just by applying the right set of quality gates.

## Real solutions require engineers to produce higher quality work

I've listed these in order, ranked by my opinion, starting with the 'best', as in most likely to have a positive impact.

### Making it easier for your engineers to do better work

I hope you saw this coming. If you want better work to happen in your engineering organisation, and you don't believe in miracles, then you need to create an environment in which people are capable of doing better work.

#### Allowing engineers to improve product quality when they know it's needed

A significant own-goal that organisations frequently score is that when they know about a quality problem, and they also have the skills available to fix it, and time to do so, they simply do not make the fix. To frustrated, paying customers, this is inexplicable and infuriating.

Step zero to improving quality is to not make this mistake. When quality problems are identified in your product, people on the ground need to be empowered to fix them, and trust that they will be supported in advocating for the good of customers and the business! This should be obvious, and yet it is hugely differentiating amongst software engineering teams. The reason is that you can't easily imitate this, and you can't buy it. It's a strategic difference between high performers and low performs, and it comes from having leaders and engineers that trust each other and work together.

#### Introducing tools that help engineers do better work

Introducing good tools can help engineers to focus on solving problems that add value (and avoid incomplete/broken re-implementations for functionality that you can get using existing tools). 

#### Streamlining processes to reduce interruptions and distractions

Hour-long standups are actually very bad.

#### Removing and mitigating high-friction developer experiences

It should be somebody's job to make everybody more productive and keep them productive by solving problems that distract them for value-adding work.

### Helping your existing engineers to become more skilled

#### Pair programming

A great way to transfer skills and create an organisation full of your best few people.

#### Empowering your known best engineers to set the engineering culture

As above.

### Hiring more skilled engineers

No matter what you do, a team of offshore developers from <budget-friendly developing nation> might not be able to acquire the skills you need them to, to deliver the quality of work you need delivered, in the time frame that you need it delivered in. You might need to hire new, local people, who are experienced, skilled, and yes, more expensive!

#### Probation periods are useful

I think it's a good sign when an engineering organisation isn't afraid to let people go when it clearly isn't a good fit. Sometimes, a person has great credentials, super relevant experience, they aced the interview, but somehow they never manage to take off on the job. This is what probation periods are for, and when a company never uses it, I think it's a bad sign. The organisation will just go on accumulating median performers forever until the organisation itself is a median performer (a fate you don't want in this economy).
