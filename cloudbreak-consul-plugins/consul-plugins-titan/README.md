# consul-plugins-titan

Recipes are not yet available using the Cloudbreak UI, thus if you’d like to try this Titan plugin, you should use the [Cloudbreak Shell](https://github.com/sequenceiq/cloudbreak-shell).

Clone the project from GitHub.

```
git clone https://github.com/sequenceiq/consul-plugins-titan.git
```

From Cloudbreak shell use (after the usual steps - use the `hint` command to drive you thorugh the steps):

```
recipe add --file /consul-plugins-titan/titan-recipe.json
```

Alternatively you can use the `--url` parameter to add the Titan recipe from an URL.

```
recipe add --url https://raw.githubusercontent.com/sequenceiq/consul-plugins-titan/master/titan-recipe.json
```

Once the recipe is as added you can check with the `recipe list` command.

For using the Titan recipe you'll have to select it.

```
recipe select --id RECIPE_ID
```

In case your blueprint and credentials have already been selected and the instance groups are configured (`instancegroup configure`), create your stack with `create stack` command. If your stack is ready, you are able to create your cluster:

```
cluster create --description "Cloudbreak provisoned cluster with Titan"
```
