import requests
import time
import struct
import json


def collect_entropy():
    timestamp = int(time.time())
    url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson"
    response = requests.get(url)

    if response.status_code == 200:
        data = response.content 

        earthquake_data = json.loads(data)

        extracted_data = {}
        if 'features' in earthquake_data and len(earthquake_data['features']) > 0:
            first_earthquake = earthquake_data['features'][0]
            if 'properties' in first_earthquake:
                properties_data = first_earthquake['properties']
                extracted_data['mag'] = properties_data.get('mag', 'N/A')
                extracted_data['time'] = properties_data.get('time', 'N/A')
                extracted_data['updated'] = properties_data.get('updated', 'N/A')
            if 'geometry' in first_earthquake:
                geometry_data = first_earthquake['geometry']
                if 'coordinates' in geometry_data:
                    extracted_data['coordinates'] = geometry_data['coordinates']

        return extracted_data

def custom_mix(data, key):
    data_to_mix = ''.join([str(byte) for byte in data])
    data_bytes = data_to_mix.encode() 

    key_len = len(key)
    mixed_data = bytearray(len(data_bytes))

    for i in range(len(data_bytes)):
        char = data_bytes[i]
        key_char = key[i % key_len]
        key_value = int(key_char)
        mixed_byte = char ^ int(key_char)
        mixed_data[i] = mixed_byte

    return mixed_data

def double_mix(data, key):
    data_bytes = data
    key_bytes = key.encode()

    mixed_bytes = bytes((x + y) % 256 for x, y in zip(data_bytes, key_bytes))

    current_time = struct.pack("<d", time.time())
    seed = bytearray((x + y) % 256 for x, y in zip(mixed_bytes, current_time))

    return bytes(seed)

def seed_to_binary(seed):
    binary_representation = ""
    for byte in seed:
        binary_byte = bin(byte)[2:]  
        padded_binary_byte = binary_byte.zfill(8)
        binary_representation += padded_binary_byte

    return binary_representation

def generate_initial_seed():
    current_time = time.time()
    time_bytes = struct.pack("<d", current_time)  
    seed = bytearray(time_bytes)  
    return seed

if __name__ == "__main__":
    seedInitial = generate_initial_seed()
    dataMixe = collect_entropy()

    data_to_mix = json.dumps(dataMixe)

    mix = custom_mix(data_to_mix.encode(), seedInitial)
    key = str(time.time() / 5)
    doublemix = double_mix(mix, key)

    with open("mix_data.txt", "w") as mix_file:
        mix_file.write(seed_to_binary(mix))

    with open("doublemix_data.txt", "w") as doublemix_file:
        doublemix_file.write(seed_to_binary(doublemix))


