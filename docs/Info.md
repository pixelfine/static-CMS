### Info\#1 : Encodage git Windows vers le Docker
>Il se peut que l'erreur ```/usr/bin/env: 'sh\r' : No such file or directory``` se produise.\
>Il faudra reconfigurer le core.autocrlf à false:\
>```git core.autocrlf false```\
>Vous pouvez alors recloner le projet et l'erreur ne devrait plus se reproduire.\
>Source : https://pauledenburg.com/docker-on-windows-usr-bin-env-bashr-no-such-file-or-directory/


### Info\#2 : Publier un Docker container:
```sh
$ docker image tag ssg_ssg username/ssg:v0.1
$ docker push username/ssg:v0.1
```
### Info\#3 : template++/structured
Notre template, en plus de répondre aux critères imposés, utilise une syntaxe particulière pour l'accès à un objet metadata,
elle supporte les tableau à dimension N, les tableau d'objets ainsi que les objets de tableau ..
>voici quelques exemple d'utilisations simple 
```toml
array4 = [ [ 1.2, 2.4 , 3.3], ["all", 'strings', """are the same""", '''type'''] ]
categories = [
	{name = "Accueil", url = "index.html"},
	{name = "Nos pains", url = "pains/"},
	{name = "A propos", url = "apropos.html"}
]
```
```html
<p> {{ metadata.array4[0][1] }} </p> renvoit 2.4
<p> {{ metadata.categories[2].name }}</p> renvoit "A propos"
```
>Pour structured, nous utilisons la librairie Jinjava (implémentation de Jinja)
>Nous pouvons utiliser de la logique encapsulé par {% %}, 
>l'accès aux données encapsulés s'effectue en l'encapsulant avec {# #}
>Voici un exemple d'utilisation :
```html
{% for category in categories %} 
     <p> {# category.name #}  </p>
{% endfor %}
```

