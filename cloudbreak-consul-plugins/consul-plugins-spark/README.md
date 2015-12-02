# consul-plugins-spark

Recipes are not yet available on the Cloudbreak UI, thus if you’d like to try this Spark plugin, we suggest to use the [Cloudbreak Shell](https://github.com/sequenceiq/cloudbreak-shell). 

Clone the project from GitHub.

```
git clone https://github.com/sequenceiq/consul-plugins-spark.git
```

From Cloudbreak shell (after the usuall steps - use `hint`):

```
recipe add --file /consul-plugins-spark/spark-recipe.json
```

Alternatively you can use the `--url` parameter to create the Spark recipe.

```
recipe add --url https://raw.githubusercontent.com/sequenceiq/consul-plugins-spark/master/spark-recipe.json
```

For using your recipe you have to select it. (you can check your recipes with `recipe list` command)
```
recipe select --id RECIPE_ID
```
In case of your blueprint and credential have already been selected and the instance groups are configured (`instancegroup configure`), create your stack with `create stack` command. If your stack is ready, you are able to create your cluster:

```
cluster create --description "Cloudbreak provisoned cluster with Apache Spark"
```

Note that: this recipe is for using Apache Spark with YARN (`yarn-cluster` or `yarn-client` mode)




