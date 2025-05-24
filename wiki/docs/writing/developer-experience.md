# Developer Experience

## Definition

The term 'developer experience' is a special case of the term 'user experience', to centre software developers specifically as the subject.

The original term ('user experience' / UX) is a tautology, and 'developer experience' or DX for short, is also a tautology.

The word 'experience' here refers to the *lived experiences* of *the people* who are the subject. A special definition of 'developer experience' is unnecessary, because it is exactly what it says it is. Developer experience is the lived experience of software developers. Obviously, it is used in the context of their work. Whatever developers experience while developing software, is part of DX. For example, using a search engine is a common developer experience. This is regardless of whether there is any connection between other development tools and the search engine. Remember, DX means "the lived experiences of developers".

Sometimes I worry that the industry is going to take a turn towards a place where companies talk about how much they care about developer experience, but what they do is quite plainly not in pursuit of improving the lived experiences of developers at work. Instead, we will return to prioritising processes and tools over individuals and interactions.

As a tautology, you would hope the meaning is so obvious that this doesn't happen. History does not offer a reassuring view here though. Other, seemingly inconfusable tautologies, have previously befallen the fate of having their meaning turned upside down. For example, the term [*continuous integration*](https://trunkbaseddevelopment.com/continuous-integration/) is now synonymous with *discontinuous*, branch-based integration.

## Who cares about DX?

Every company that needs to create software products of any kind, for anyone, needs to think about the lived experience of the developers who build those products, whether they are employees, contractors, customers, or their customers' customers. [A recent article I read](https://kenneth.io/post/developer-experience-infrastructure-dxi) seemed to suggest that DX is something that only developer tooling companies need to think about. I don't agree with this.

## Why care about DX?

### Bullet points

The importance of DX is not universally intuitive. If it were, I would not be writing this in a huff. Here are some bullet points, which reflect what I think about, when I think about the importance of DX prioritisation.

Why would you want to improve the lived experience of the developers who build the software?

- Unhappy, unmotivated workers produce worse products, slower. Companies full of such workers achieve worse outcomes compared to competitors with happier, more motivated workers.
- Good developers leave companies with poor DX, specifically to move towards companies with better DX. The trend is that companies which don't care about DX either lose or lack the talent required to create and maintain competitive software products.
- Removing obstacles and automating rote tasks (i.e. reducing toil) has great cost-saving and innovation-enabling qualities:
    -  Reducing toil saves time during the day for a developer that they would otherwise spend doing something that a machine can do better. This daily time saving scales across the number of developers affected by the toil. Even if you plug in only small numbers for the time savings, the scaling factor can multiply this into significant savings per unit time.
    - Reducing toil removes cognitive encumbrances on your developers that enable them to focus on important problems more deeply, further boosting productivity.
    - Reducing toil exposes potential opportunities for product or technical innovation, by freeing up time and cognitive ability that is otherwise locked up in unproductive busywork. There is a great opportunity cost associated with leaving toil to build up, without prioritising its continuous removal. I haven't seen this aspect of toil reduction included in existing writings on the subject of DX.


### Intuition

For ['99% Developers'](https://future.com/software-development-building-for-99-developers/), their lived experience in their workday is routinely dismissed out of hand, in the unending prioritisation battles that are being fought in their workplaces, such as banks, insurance companies, and the public sector. More on this later.

There is research, such as that in [Accelerate](https://itrevolution.com/product/accelerate/), which shows statistically significant correlations between prioritisation of fairly minor developer experience improvements, and improved organisational performance across commercially important metrics (e.g. profitability). The lead researcher of this work, Nicole Forsgren, is also an author of [the 'SPACE' paper about metrics for developer productivity](https://queue.acm.org/detail.cfm?id=3454124).

Those engineering organisations that are waiting to be spoon-fed the crunched data from researchers, are being left behind by those who intuitively understand that DX matters if you want to compete efficiently in the market of software products.

[Long before](https://en.wikipedia.org/wiki/Extreme_programming#History) Accelerate, or any widely recognized research in the area had been done, many developers had been talking about the importance of things like version control, continuous integration, and continuous delivery. They would still have continued advocating for them in the absence of formal research such as that done by the DevOps Research and Assessment (DORA) team. For many people, the importance of centering the lived experiences of developers in order to improve their productivity is intuitive. For me it is a basic value that people should be empowered to be able to make their work more productive and enjoyable.

## DX forces

I think the tooling aspect of developer experience is over-represented in the available writings about DX online. This subsection is about a few other aspects.

An important aspect of DX that I don't see much written about is the habitability of the codebase. Those few who have been so blessed to work with [wonderfully well-tested](/writing/tests) and thoughtfully designed systems know how much difference to your experience and your productivity it makes, to work on a piece of code with great automated tests, that are cleanly refactored, such that useful abstractions are readily available to set up and write the next automated test. This is helpful when trying to reproduce and fix bugs, when extending an existing feature, and when adding new features.

The presence of abstractions in the codebase, independent of tools, is another contributor to DX that I think most companies don't take explicit care of. I don't foresee this entering the cultural mainstream of the industry for while though.

Amazing, incredibly productive developer experiences within your codebase don't appear out of nothing. They need to be tended to, every day, especially in fast-growing, or already large teams, where the tendency for divergence is greater.

Team structuring and sizing is also important. You can't adjust them too frequently because you'll destabilise your delivery system, but you can occasionally, or even periodically tune them in order to address (proactively or reactively) DX challenges you're facing. I really liked [Team Topologies by Mathew Skelton and Manuel Pais](https://teamtopologies.com/book) on this topic.

## DX apathy culture

A great number of huge and highly profitable traditional businesses, know that building software products is essential for their continued success.

Efforts have yielded mixed results. I have consulted at two banks, whose retail (i.e. consumer-facing) banking apps had ~2 star ratings at the time when I (temporarily) joined. Making products that are compelling, well-designed and free of bugs, is really hard. It's a lot harder when you have a raise a support ticket to download your IDE, or if you have to run and maintain an instance of Mattermost for your team's use because there are no approved internal communication tools aside from Skype messenger (a tool which does not persist messages across client sessions, and therefore is essentially useless for a team).

Such businesses struggle to produce good software products, and it is really no surprise. There are a few characteristics of these engineering organisations which I consider "tell-tale signs" of a culture that does not care about the experience of developers. I claim here only correlation, and make no claim as to a cause:

- Reliant on external software vendors for development
- Work is organised around [projects, and not products](https://www.madetech.com/blog/products-not-projects/)
- Political game-playing and career-driven maneuvering is commonplace aka ['The Hot Potato program management methodology'](https://www.waterfall2006.com/kale.html)
- Highly regulated industry (although the regulations don't usually specify that the software must be awful)
- Other people, who are not developers, also complain about being unhappy

## Takeaways

1. 'Developer experience' is a tautology. It refers to the lived experiences of developers.
2. Every company that builds software products of any kind (including apps, websites, eCommerce things etc.) should be interested in fostering great experiences for their developers.
3. It should be intuitively clear that DX is important for the success of a company that builds and depends on its software products for revenue, but if not, there is also compelling data.
4. DX is not only about tools. Codebase habitability, abstraction and team structure are also important.
5. In my experience, companies with the worst DX often have a few other curious traits in common.
