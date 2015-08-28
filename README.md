
`evopoe` is a tool for generating character builds for Path of Exile. It
searches the skill tree and attempts to find a build that meets your needs.

## Scoring Function

In order for `evopoe` to generate a build for you, you'll need to pass it
some parameters it can use to generate a score for each build. This is done
by passing command line arguments.


*(Note: If you're running via Eclipse you can modify the "Run Configurations" and
specify these in the "Arguments" tab.)*

Each argument allows you to set a weight for mods that are matched in the skill tree.

For example, if you really want to get a lot of life you may try:

	"Increased Maximum Life=50"

This is going to give a weight of `50` for any skill node that
contains the "Increased Maximum Life" modifier. Internally all the extra
whitespace is removed and everything is lowercase, so this is the same
as:

	"increasedmaximumlife=50"

You can pass as many as you want. If you omit the weight (`=50`) it will
default to a weight of 1.0. By default exact text matching is used. If you
want to do more complex matching include a '*' somewhere in the pattern:

	"*withbows*=25"

If you want to give a penalty to avoid certain types of mods use a negative
weight:

	"*spendlifeinsteadofmana*=-1000"

The example above ensures that a build is penalized for picking Blood Magic.

Note that if you're writing many rules you should order the rules such that
penalties come before negative weights. For example:

	"*dualwielding*=-500"
	"additionalblockchancewhiledualwieldingorholdingashield=100"

In this case you want to penalize dual wielding in general, but not the specific
case of also getting block on a shield.


