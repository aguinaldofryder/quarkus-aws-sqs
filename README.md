# Filas com Quarkus e AWS SQS
## Visão geral
Este exemplo mostra como implementar uma fila com Quarkus e AWS SQS. Foi uma PoC para validação de um fluxo de atualização de produtos, onde o usuário insere e altera produtos em um sistema em nuvem e deve receber tais atualizações em um sistema que pode funcionar sem conexão com a internet. 
Ao conectar novamente deveria sincronizar as alterações, na mesma ordem em que aconteceram.

Além dos produtos, também foi sincronizado outras informações, como clientes e condições de pagamento. Era necessário conectar vários clientes ao mesmo servidor, por isso foi optado por criar uma fila para cada cliente e para reduzir custos e melhor organizar as filas, optou-se por criar apenas uma fila por cliente e utilizar um cabeçalho para identificar o tipo de dado de cada mensagem.

## Tecnologias
* Java 11
* Maven 3.8.1
* Quarkus
* AWS SQS

## Como executar
Para este exemplo utilizou-se de uma imagem docker para simular a fila SQS. 

```docker run --rm --name local-sqs -p 8010:4576 -e SERVICES=sqs -e START_WEB=0 -d localstack/localstack:0.11.1```

Cofigure as credencias
```
aws configure --profile localstack
AWS Access Key ID [None]: test-key
AWS Secret Access Key [None]: test-secret
Default region name [None]: us-east-1
Default output format [None]:
```

Execute o produtor de mensagens (amazon-sqs-producer).
```
cd amazon-sqs-producer
mvn clean package
java -jar ./target/quarkus-app/quarkus-run.jar
```

Após iniciado crie uma nova fila com o nome `quarkus-sqs.fifo`
```
curl --request POST --url http://localhost:8080/queue --header 'Content-Type: application/json' --data quarkus-sqs.fifo
```

Agora execute o receptor de mensagens.

```
cd amazon-sqs-consumer
mvn clean package
java -jar ./target/quarkus-app/quarkus-run.jar
```

Pronto. Agora você já pode enviar mensagens para a fila.
```
curl --request POST --url http://localhost:8080/queue/message --header 'Content-Type: application/json' --data '{"id": "123",	"description": "Example"}'
```

Verifique no terminal onde o consumer está sendo executado e veja as mensagens sendo mostradas.
