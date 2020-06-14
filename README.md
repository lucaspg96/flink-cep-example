# Flink CEP Example

Este repositório contém um exemplo prático de *Complex Event Processing* com Flink. 
Neste exemplo, iremos processar a rota de transporte de produtos. Para simplificar, existem algumas regras:
* Todo transporte de um produto tem um ponto de partida e um ponto de destino;
* Toda rota inicia-se com um transporte partindo de **A**;
* Toda rota encerra-se ao chegar em um ponto **B**;

O código deste repositório consome os transporte de um tópico do Apache Kafka, onde as mensagens estão em Json.
Há também um exemplo pronto no código de um a viagem. 

### Gerador de dados

Neste repositório encontramos um script python para gerar dados sintéticos de viagens para serem inseridas no Kafka.
Encontramos também um arquivo de configurações para executar o Kafka utilizando o docker-compose. 
Por padrão, o Kafka está ouvindo requisições apenas em `localhost:9092` e o tópico utilizado chama-se `transports`.

Para iniciar o kafka, rode o comando `docker-compose -f kakfa-docker-compose.yml up -d`

Para executar o produtor de dados, é necessário ter instalada a lib `kafka-python==1.4.7`. 
Uma vez que a lib esteja instalada, basta executar `python transport-data-producer.py`.