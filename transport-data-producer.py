from random import randint

product_id = 0

def generate_transport(src, dst):
    return '''{{
    "productId": {},
    "from": "{}",
    "to": "{}"
    }}'''.format(product_id, src, dst).encode("utf-8")

def generate_travel():
    global product_id
    travel_size = randint(0, 10)
    travel_size = 10
    product_id += 1
    traj = ["A"]
    for i in range(0, travel_size):
        traj.append("M{}".format(i))

    traj.append("B")

    return [generate_transport(traj[i],traj[i+1]) for i in range(len(traj)-1) ]

from kafka import KafkaProducer
import time
producer = KafkaProducer(bootstrap_servers='kafka:9092')

while True:
    [producer.send("transports",m) for m in generate_travel()]
    time.sleep(5)