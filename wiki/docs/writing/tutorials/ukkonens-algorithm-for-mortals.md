# Ukkonen's Algorithm for Mortals

## What is Ukkonen's algorithm?

Ukkonen's algorithm is an online suffix tree construction algorithm which runs in time and space that is linear with the length of the string being indexed. Below I'll break this sentence down into parts.

- Ukkonen's is a suffix tree construction algorithm. This means that it accepts a string input, and as an output it produces a suffix tree for that string. There is plenty written about suffix trees, and the other explanations will be better than mine, so please look for them instead.

- Ukkonen's runs in time and space that is linear with the length of the input string. This means that as the length of the input increases, the corresponding increase in storage space for the resulting data structure, and the running time for its construction, increases only proportionally, and not more than that. In practice, it means that the algorithm is effective for use on much larger inputs than super-linear algorithms, which experience prohibitive costs when running for inputs above a certain length.

- Ukkonen's is an online algorithm. This means that each iteration of the algorithm yields a result which is correct (or can be made correct in constant time) for the input that has been processed so far.

## Why does Ukkonen's algorithm exist?

At the time of Ukkonen's algorithm, the other known linear-time algorithms for suffix tree construction were more difficult to understand, and they lacked the desirable online property. Of the existing linear-time suffix tree construction algorithms, Ukkonen's is generally recognised to be the easiest to understand. Of course, that is highly subjective. I didn't learn the other algorithms, so I can't tell you for sure. In my opinion this one is complicated enough.

## Prerequisite knowledge

I will assume that you know what a suffix tree is. The descriptions online and in YouTube are good enough to understand what a suffix tree is, even if they are insufficient for understanding Ukkonen's construction algorithm. It would also be helpful for you to feel comfortable with the much simpler, naive, cubic time construction algorithm which is often presented as an introductory algorithm on the way to Ukkonen's.

## High-level overview

In Ukkonen's algorithm, we construct the tree iteratively, considering one character at a time from the input, scanning from the start to the end. We start with just a root node. For each new character, we extend the tree we currently have, in order to express the new suffixes in it.

## Implicit vs canonical suffix trees

Each step in the algorithm produces an implicit suffix tree. An implicit suffix tree is different to a true suffix tree, aka a canonical suffix tree. An implicit suffix tree still contains all suffixes of the string, but not all of those end in a leaf node. That is, some suffixes are represented by paths in the tree from the root node, which terminate within edge labels in the tree. In order to utilise some suffix tree algorithms, we would really like to construct a canonical suffix tree, so the last step in Ukkonen's algorithm is tree canonization, which transforms the implicit suffix tree into a canonical suffix tree.

Each implicit suffix tree produced by the algorithm in each iteration corresponds to a prefix of the input string, and every iteration's implicit suffix tree is built on top of the one from the previous iteration.

## Suffix tree canonization

To canonize an implicit suffix tree, we can add a unique character to the suffix tree at the end, after all our input has been processed and incorporated into the implicit suffix tree that we're iterating on. In almost all descriptions, a dollar symbol is used (i.e. '$'). In practice, my implementation uses a null character (i.e. '\u0000').

## Remainder

Throughout the algorithm, a positive integer variable stores the number of suffixes of the so-far-consumed input string, which are not explicitly expressed in the tree. Equivalently, it represents the number of suffixes that are expressed in the tree only implicitly, and not explicitly. Every phase begins by incrementing this counter, since we know that once we add a new character to the string, there is an additional implicitly represented suffix in the tree. In most descriptions of Ukkonen's algorithm, this variable is called something like "remainder". In my implementation, it is called "remainingSuffixes". Call it whatever you're comfortable with.

## Phases and Extensions

The algorithm's iteration contains Phases and Extensions. For each character we are adding, we perform a Phase. For each Phase, we perform one or more Extensions. The Phase is the outer loop, and the Extension is the inner loop.

Every Phase corresponds to a character in the input string, and equivalently, a prefix of the input string. Every Extension corresponds to a suffix of the Phase's corresponding prefix of the input string.

Something to note is that while a Phase corresponds to a prefix of the total input string, it corresponds to the
entirety of the string that is incorporated into the implicit suffix tree at the end of the Phase. Another way to think
about it is that in each Phase, every suffix of the so-far consumed input string is inserted into the tree, either
implicitly or explicitly. The insertion of these suffixes to the tree is done using Suffix Extensions.

## Suffix Extensions

When we express new suffixes, we perform an action to the tree called a Suffix Extension. In a Suffix Extension, we add a new suffix of the so-far consumed input string, into the suffix tree. After the Suffix Extension, the suffix will be either implicitly or explicitly represented in the implicit suffix tree. There are a few different kinds of Suffix Extension.

- Rule 1 Extension: When the suffix we're adding can be added by extending the labels of edges leading to leaf nodes.
- Rule 2 Extension: When the suffix we're adding requires the addition of a new leaf node.
- Rule 3 Extension: When the suffix we're adding is already implicitly present in the tree, even if it is not explicitly
  represented.

Only Rule 2 Extensions result in new suffixes being made explicit in the tree. During the algorithm, while we're keeping track of the number of suffixes remaining from the portion of the input which has been processed, we need to remember to decrement this counter after each time that we perform a Rule 2 Extension.

When we do a Rule 3 Extension, it means that the suffix we were adding to the tree is already implicitly present in the tree. This additionally means that all the suffixes of that suffix are also implicitly present in the tree, which means that none of them would result in a Rule 2 Extension. So a Rule 3 Extension ends the current Phase.

In every Extension of every Phase, exactly one Suffix Extension happens. That is why the inner loop iterations of the algorithm are called Extensions.

## First Extension

The first extension of every phase is always the same. It involves extending all labels of edges to leaf nodes, so that they end in what is now the new last character in the string represented by the implicit suffix tree. The first extension of every phase is a Rule 1 Extension, and no other extensions are ever Rule 1 Extensions.

## Second Extension onwards

In order to run in linear time, Ukkonen's algorithm depends on being able to perform each Suffix Extension in constant time. In order to do this, every extension needs to avoid traversing the tree to find the right spot to perform any modifications to the tree. Instead, it needs to be able to find the right place in the tree to perform the Suffix Extension, in constant time. Solving this problem is key in Ukkonen's algorithm.

The naive way to complete the Suffix Extensions in the second Extension onwards, would be to traverse the tree for each insertion, in order to find the correct place to make the Suffix Extension. This would work correctly, but traversing the tree is expensive. If we did that for all extensions, we would end up with an algorithm whose worst case running time is cubic in the length of the input string. What we're looking for is an algorithm whose worst case running time is linear in the length of the input string. Ukkonen's description of his algorithm begins with a description of this cubic-time algorithm, and presents the actual Ukkonen's algorithm as a collection of optimizations on top of it. There are two key parts of Ukkonen's optimization over the naive algorithm. They are the Active Point, and Suffix Links.

The Active Point, combined with the use of Suffix Links, enables us to be able to complete each Extension in constant time. The Active Point locates the precise insertion point for the next Suffix Extension, and Suffix Links store exploitable information about the suffixes in the text.

## Active Point

The Active Point is a reference to a precise location in the tree with a special property, that it will be the correct location for the next Suffix Extension to proceed from.

This reference is represented as a triple of "activeNode", "activeEdge" and "activeLength". These three pieces of data combined allow us to pinpoint an exact location in the tree (and therefore an exact location in the text), for example, directly on top of an internal node, or perhaps partway into an edge label.

- The "activeNode" variable is a pointer to either the root node, or an internal node in the tree.

- The "activeEdge" variable is a reference to a character in the text, used to indicate which edge of the tree node indicated by the "activeNode" we need to move along to reach the precise position of the Active Point. At any point in time, the "activeEdge" should be pointing to a character in the text within the suffix that is being added in the current Extension.

- The "activeLength" variable indicates how far we need to travel along the edge indicated by the "activeEdge", in order to get to the precise position of the Active Point.

## Suffix Links

A Suffix Link represents a pointer going from one internal node in the tree to another internal node, such that the path to the destination node is a suffix of the path to the source node. The destination node is said to be the Suffix Link of the source node. A node can have no more than one Suffix Link. If no Suffix Link exists for a node, then it has an implicit Suffix Link going to the root node. In a given tree, many nodes will likely have a Suffix Link. Suffix Links are essential information that we store in the tree for Ukkonen's algorithm. Even when implementing other suffix tree algorithms, we may still be able to exploit the Suffix Links that were created during the tree's construction.

A given node can only have one Suffix Link. Also, there is only ever one node with a Suffix Link to a given node. Another way I would phrase this is that a node can have no more than one inbound Suffix Link pointer, and no more than one outbound Suffix Link pointer. A node may have both an inbound Suffix Link pointer and an outbound Suffix Link pointer, that is, it may both be the Suffix Link node for another node, and also have its own Suffix Link node.

## Constant-time traversal using the Active Point and Suffix Links

When we perform Suffix Extensions, rather than traversing the tree from the root to find the insertion point, we instead traverse from the Active Point. In order for the algorithm to be correct, the Active Point needs to move around the tree and at the start of each Extension, it needs to somehow be exactly where it needs to be. After every Suffix Extension, two things need to happen.

- The Active Point moves through the tree to ensure that it is in the right place. It may follow Suffix Links when doing this movement.

- Suffix Links are lain down, so that future iterations will be able to traverse the tree correctly.

In both of these actions, different things will need to be done depending on the Extension Rule that was applied for the Suffix Extension.

## Active Point movement after Extensions

After performing a Suffix Extension, we need to make sure that the Active Point is in precisely the right place before starting the next Suffix Extension. The hard work to figure out how to do this was done by Ukkonen in his paper, in which he provides how to move the Active Point, and the proof for why it ensures correctness. For implementors, it suffices to

- On Rule 3 Extension: Whenever we perform a Rule 3 Extension, our Active Point advances one position down the tree in its current direction. This is often implemented as incrementing the "activeLength" variable.

- On Rule 2 Extension from root: Whenever we perform a Rule 2 Extension where the Active Point is along an outbound edge from the root node (but not directly on the root node), then we step the Active Point one position backwards and pull the active edge forwards to the first character of the next suffix being added. This is often implemented as decrementing the "activeLength" variable, and setting the "activeEdge" variable to point to the first character of the next suffix to be added, which you can find by stepping back from the end of the currently processed input, by a number of steps equal to the number of remaining suffixes that are not explicit in the tree.

- On Rule 2 Extension from an internal node: Whenever we perform a Rule 2 Extension where the Active Point is either on an internal node, or on an outbound edge from an internal node, then the Active Point follows the Suffix Link from that internal node. As mentioned earlier, if an internal node hasn't yet been assigned a Suffix Link, it has an implicit Suffix Link which is the root node, so in that case, the Active Point would go to the root node. This is often implemented by setting the "activeNode" variable to equal the node pointed at by the Suffix Link, or if there's none, then "activeNode" gets set to the root node.

## Suffix Link placement after Extensions

For the Active Point to be able to move around the suffix tree as it needs to, we need to lay down links between certain nodes in the tree. Specifically, if a node has a path from the root whose edge labels spell out a suffix of some other node's path from the root, then the former node is called the Suffix Link node of the latter. Often, the term Suffix Link is used to describe the destination node itself, and not just the link between the nodes.

As mentioned earlier, every internal node has an implicit Suffix Link to the root node. Leaf nodes do not have Suffix Links.

In each Phase, we create a Suffix Link chain, also called a Suffix Chain, which is a linked list of nodes that are connected by Suffix Links. Every time we create a new Suffix Link, the destination node of the link becomes the source node for the next Suffix Link. For this reason, rather than writing about adding a Suffix Link, I will write about adding a node to the Suffix Chain. This makes it clearer that every time a node is added, it enters the role of both a destination for a Suffix Link, and potentially a source, if there is another Suffix Link added before the end of the Phase.

Suffix Links are created as a part of all Rule 2 and Rule 3 Suffix Extensions. This is because during any Phase, in every Extension from the second Extension onwards, the suffix of the input string being added to the tree will also be a suffix of the string added in the previous Extension (which is itself a suffix of the input string).

The following description of the Suffix Link placement scenarios will assume that the reference to the Active Point is canonical (see next section to understand Active Point "canonical references") at the time when the Suffix Extension is performed on the tree.

- On Rule 3 Extension: For the edge that the Active Point along, add the source node of that edge to the Suffix Chain. This will not result in repeatedly adding the same node to the suffix chain, because a Rule 3 Extension will also result in the end of the Phase, and so the Suffix Chain will be reset.

- On Rule 2 Extension: For the leaf node that was inserted, add its parent node to the Suffix Chain.

## Active Point non-canonical references

As mentioned earlier, the Active Point reference is stored with three variables: "activeNode", "activeEdge", "activeLength". It is possible for the algorithm to reach a state where the "activeLength" indicates a length which is longer than the label for the edge indicated by the "activeEdge" variable. In this case, the position of the Active Point is somewhere beyond the end of that edge. A reference in this form still correctly describes a precise location for the Active Point, but we have to traverse the tree to get there. This kind of reference is called a non-canonical reference.

## Active Point reference canonization

If we find ourselves in a position where we're about to perform a Suffix Extension from a non-canonical reference, we need to stop and canonize our reference first. I don't have a rigorous argument for this, but I can confidently say that attempting to implement all the Active Point movements and Suffix Link placements from a non-canonical reference is more
difficult to get right, or to follow as a reader of the code.

Canonizing an Active Point reference is an iterative process, by which you traverse edge labels in the tree and convert your current reference into a reference which is progressively closer to the canonical one. One way to think about this, is that while you are canonizing an Active Point reference, the "activeLength" variable in your Active Point reference should always be decreasing, the "activeEdge" should point to a character closer to the end of the suffix being added (i.e. closer to the end of the input string), and the "activeNode" should move closer to the base of the tree.

For me, the "activeEdge" was the trickiest to think about here. Something that really helped me was when I stopped thinking of it as a character, and instead treated it like an index into the text. For character comparisons at nodes, my implementation dereferences the "activeEdge" index of the input string to get the underlying character.

## Active Point behaviour around node boundaries

If your Active Point is walking down an edge label, and reaches the next internal node, it is important to understand how the next Suffix Extension behaves. There are a few different ways to handle this in implementation, but the essential crux is making sure that all the variables in your Active Point reference are consistent with each other, and that whatever they are, the rest of the implementation is expecting the way that you handle it in the reference, including the Suffix Extension implementations, and also your Active Point reference canonization.

For example, in my implementation, when the Active Point reaches the next node (i.e. the "activeLength" is at least as big as the length of the edge referenced by the "activeEdge"), my implementation will initiate Active Point reference canonization. This means that all of my Suffix Extension logic is expecting the Active Point reference to be canonical.

## Remarks

This algorithm is not like many of the other, more trivial algorithms that I met in undergraduate computer science, such as back-propagation, spanning tree construction, or the simplex iteration. One of the most difficult aspects for me coming into this algorithm was that lack of high-quality information. No other tutorial-like resource besides this one, as far as I am aware, makes it clear exactly when Suffix Links need to be added.

This title for this comes from the book "Q for Mortals" by Jeffry Borror.

## Appendix A: The most useful sources I used when I was implementing this algorithm

- Ukkonen's in plain english: https://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
- Tushar's tutorial on Ukkonen's: https://www.youtube.com/watch?v=aPRqocoBsFQ
- Ben Langmead's computational genomics lecture on Suffix tries and trees: https://www.youtube.com/watch?v=hLsrPsFHPcQ
- Ukkonen's original paper: https://www.cs.helsinki.fi/u/ukkonen/SuffixT1withFigs.pdf
- Brenden Kokoszka's visualisation tool: http://brenden.github.io/ukkonen-animation/

## Appendix B: Testing your implementation for correctness

By hand, it will be difficult for you to come up with all the test cases to prove that your implementation is fully correct. I did attempt this. You can get quite far with it, but I never managed to get all the way to a robustly correct implementation by going case-by-case. I used fuzz testing in order to determine correctness.

For fuzz testing, I recommend these guidelines:

- Use a small alphabet of characters to produce input strings
- Attempt more inputs of smaller length

Both of these mean that the pathological cases your fuzzer finds will be simpler, which will make them easier to replay for debugging.

## Appendix C: Implementation with comments

This is object-oriented Kotlin.

```
class SuffixTree {
    private var currentlyInsertedInput = ""
    private var remainingSuffixes = 0

    private val rootNode = RootNode()
    private val activePoint = ActivePoint()

    companion object {
        fun ukkonenConstruction(input: String): SuffixTree {
            val suffixTree = SuffixTree()
            input.forEach { suffixTree.addChar(it) }
            suffixTree.canonize()
            return suffixTree
        }
    }

    fun addChar(c: Char) {
        // Add the character to the string of characters whose suffixes are present in the tree
        // already.
        currentlyInsertedInput += c

        // There is now an additional suffix which is not yet explicit in the tree, so we increment
        // our counter for the number of remaining suffixes.
        remainingSuffixes++

        // We ask the active point to add the remaining suffixes, with each suffix able to be added
        // in O(1) time because the active point is creating and exploiting knowledge about the
        // string's suffixes through the use of suffix links.
        activePoint.addRemainingSuffixes(c)
    }

    /**
     * Finds the offsets of the given query string within the input string by exploiting the built
     * tree.
     */
    fun offsetsOf(queryString: String): Set<Int> {
        return rootNode.offsetsOf(queryString)
    }

    /**
     * This converts the implicit suffix tree into a canonical suffix tree by adding a character
     * that doesn't appear elsewhere in the input.
     */
    private fun canonize() {
        addChar('\u0000')
    }

    override fun toString(): String {
        return "SuffixTree(rootNode={\n$rootNode\n})"
    }

    inner class ActivePoint {
        private var activeNode: Node = rootNode
        private var activeLength = 0
        private var activeEdge = 0

        private var suffixLinkCandidate: Node? = null

        /**
         * Add the suffixes that still need to be made explicit in the tree.
         *
         * @param c The character we're adding to the tree in the current phase.
         */
        fun addRemainingSuffixes(c: Char) {
            // We only add suffix links within a phase, so we reset it at the start of the phase.
            suffixLinkCandidate = null

            while (remainingSuffixes > 0) {
                val extensionRuleThatWasApplied = addSuffix(c)
                if (extensionRuleThatWasApplied == SuffixExtensionRule.RULE_THREE) {
                    // When we apply rule three extensions, it ends the current phase because the
                    // suffix we want to add is already implicitly present in the tree.
                    break
                }

                // Since we haven't broken out of the loop, it means we didn't do a rule three extension,
                // which means that a new suffix has been made explicit within the tree. When this
                // happens, we decrement the number of suffixes that still need to be added.
                remainingSuffixes--

                if (activeNode == rootNode && activeLength > 0) {
                    // When we insert a node from root, we decrement our active length, and pull our
                    // active edge forwards to point at the start of the next suffix we're adding.
                    activeLength--
                    activeEdge = currentlyInsertedInput.length - remainingSuffixes
                } else {
                    // When we insert a node from an internal node, we follow its suffix link if it has
                    // one. The default suffix link for any node is root.
                    activeNode = activeNode.suffixLink()
                }
            }
        }

        /**
         * @param c The character we're adding to the tree in the current phase.
         * @return Which suffix extension rule was applied in order to add the next suffix.
         */
        private fun addSuffix(c: Char): SuffixExtensionRule {
            if (activeLength == 0) {
                // If we're at a node, set our active edge at the last added character in the text.
                activeEdge = currentlyInsertedInput.length - 1
            }

            val activeEdgeLeadingChar = currentlyInsertedInput[activeEdge]
            val nextNode = activeNode.edges[activeEdgeLeadingChar]
            if (nextNode == null) {
                // If the active node doesn't yet have a child node corresponding to the next
                // character, add one. When we perform a leaf insertion like this, we need to add a
                // suffix link.
                activeNode.edges[activeEdgeLeadingChar] = LeafNode()
                addSuffixLink(activeNode)
                return SuffixExtensionRule.RULE_TWO
            } else {
                // If the reference to the active point is non-canonical, then canonize it by
                // recursively stepping through the tree, and then go to the next extension of the
                // current phase so that we can do all our steps from the basis of a canonical
                // reference to the active point.
                val edgeLength = nextNode.edgeLength()
                if (activeLength >= edgeLength) {
                    activeEdge += edgeLength
                    activeLength -= edgeLength
                    activeNode = nextNode
                    return addSuffix(c)
                }

                // If the character is already present on the edge we are creating for the next
                // node, then we perform a rule three extension. We add a suffix link to the active
                // node, so the active point gets to the right place after our next node insertion.
                if (currentlyInsertedInput[nextNode.start + activeLength] == c) {
                    activeLength++
                    addSuffixLink(activeNode)
                    return SuffixExtensionRule.RULE_THREE
                }

                // We split the active edge by creating a new internal node and a new leaf node for
                // the next character. We add a suffix link for the newly created internal node.
                val internalNode = Node(nextNode.start, nextNode.start + activeLength)
                activeNode.edges[activeEdgeLeadingChar] = internalNode
                internalNode.edges[c] = LeafNode()
                nextNode.start += activeLength
                internalNode.edges[currentlyInsertedInput[nextNode.start]] = nextNode
                addSuffixLink(internalNode)
                return SuffixExtensionRule.RULE_TWO
            }
        }

        private fun addSuffixLink(node: Node) {
            suffixLinkCandidate?.linkTo(node)
            suffixLinkCandidate = node
        }
    }

    open inner class Node(var start: Int, private var end: Int) {
        private var suffixLink: Node? = null

        val suffix = currentlyInsertedInput.length - remainingSuffixes
        var edges = mutableMapOf<Char, Node>()

        fun edgeLength(): Int = minOf(end, currentlyInsertedInput.length) - start

        fun suffixLink(): Node = suffixLink ?: rootNode

        fun linkTo(node: Node) {
            suffixLink = node
        }

        fun edgeLabel(): String =
            currentlyInsertedInput.substring(start, minOf(end, currentlyInsertedInput.length))

        override fun toString(): String {
            return toString(1)
        }

        open fun toString(indentationLevel: Int): String {
            return "Node(start=$start, end=$end, suffix=$suffix, hasLink?=${suffixLink != null}, label=${edgeLabel()}, " +
                    "edges:${
                        edges.map {
                            "\n${"\t".repeat(indentationLevel)}'${it.key}'=${
                                it.value.toString(
                                    indentationLevel + 1
                                )
                            }"
                        }
                    })"
        }
    }

    inner class LeafNode : Node(currentlyInsertedInput.length - 1, Int.MAX_VALUE) {
        override fun toString(): String {
            return "LeafNode(start=$start, end=end, suffix=$suffix, label=${edgeLabel()})"
        }

        override fun toString(indentationLevel: Int): String {
            return toString()
        }
    }

    inner class RootNode : Node(-1, -1) {
        fun offsetsOf(queryString: String): Set<Int> {
            var i = 0
            var node: Node = this
            while (i < queryString.length) {
                // We try to follow the edge to the next internal node. If there is no such edge, then
                // there are no matches and we return the empty set.
                node = (node.edges[queryString[i]] ?: return setOf())

                // If the edge we just followed is longer than the remainder of the query string, then
                // we get matches only if the edge label starts with the remainder of the query string.
                // Otherwise we get no matches
                if (node.edgeLength() >= (queryString.length - i)) {
                    var j = 0
                    while (j < queryString.length - i) {
                        if (queryString[i + j] != currentlyInsertedInput[node.start + j]) {
                            return setOf()
                        }
                        j++
                    }
                    return suffixesUnderSubtreeRootedAt(node)
                }

                // If the edge we just followed doesn't have an edge label matching the query string,
                // from the current character until the end of the edge label, then there are no
                // matches.
                var k = 0
                while (k < node.edgeLength()) {
                    if (queryString[i + k] != currentlyInsertedInput[node.start + k]) {
                        return setOf()
                    }
                    k++
                }

                // We increase i by the size of the matching edge label we just crossed.
                i += node.edgeLength()
            }

            // If we make it out of the loop, then we have consumed the full query string by traversing
            // edges from the root. This means that all the suffixes stored within the current subtree
            // will be prefixed by the query string.
            return suffixesUnderSubtreeRootedAt(node)
        }

        private fun suffixesUnderSubtreeRootedAt(node: Node): Set<Int> {
            return if (node.edges.isEmpty()) {
                setOf(node.suffix)
            } else {
                node.edges.flatMap { suffixesUnderSubtreeRootedAt(it.value) }.toSet()
            }
        }

        override fun toString(): String {
            return "RootNode(edges:${edges.map { "\n\t'${it.key}'=${it.value.toString(2)}" }})"
        }
    }
}

private enum class SuffixExtensionRule {
    /**
     * Rule one extensions happen in the first extension of every phase, when the leaf nodes are
     * implicitly extended by the addition of the next character. I have included it here for my
     * documentation of the algorithm.
     */
    @Suppress("unused")
    RULE_ONE,

    /**
     * Rule two extensions happen when the suffix is not implicitly present in the tree, so we add
     * it in a new leaf node.
     */
    RULE_TWO,

    /**
     * Rule three extensions happen when the suffix is already implicitly present in the tree, in
     * which case we do nothing. These suffixes will be made explicit later when the tree is
     * canonized by the addition of a unique character at the end.
     */
    RULE_THREE
}
```
