
import string
import collections
import tensorflow as tf
import numpy as np
import random
import glob_config
import sys
'''
reference : http://adventuresinmachinelearning.com/word2vec-tutorial-tensorflow/
'''

filename = './text8'


'''
read data into list of strings
'''
def read_data(filename):
    """Extract the first file enclosed in a zip file as a list of words."""
    table = string.maketrans('','')
    with open(filename) as f:
        data = tf.compat.as_str(f.read()).translate(table, string.punctuation).split()

    return data
if len(sys.argv) <= 1:
    vocabulary = read_data(glob_config.training_data_path)
else:
    vocabulary = read_data(sys.argv[1])

print('data readed ' + str(len(vocabulary))+ ' lines')


'''
transfer string to id in data
build id --> word dic
build word --> id dic
any words not within the top 10,000 most common words will be marked with an UNK designation
'''

def build_dataset(words, n_words):
    """Process raw inputs into a dataset."""
    count = [['UNK', -1]]
    counter = collections.Counter(words)
    n_words = int(len(counter) * 0.95)
    glob_config.vocabulary_size = n_words
    count.extend(counter.most_common(n_words))
    dictionary = {}
    for word, _ in count:
            if word not in dictionary:
                dictionary[word] = len(dictionary)
    data = list()
    unk_count = 0
    for word in words:
        if word in dictionary:
            index = dictionary[word]
        else:
            index = 0  # dictionary['UNK']
            unk_count += 1
        data.append(index)
    count[0][1] = unk_count
    reversed_dictionary = dict(zip(dictionary.values(), dictionary.keys()))

    id_to_title_map = {}
    with open(glob_config.id_title_map_path) as id_title_map:
        for line in id_title_map:
            k, v = line.split()
            id_to_title_map[k] = v
    return data, count, dictionary, reversed_dictionary, id_to_title_map


glob_config.data, glob_config.count, glob_config.dictionary, glob_config.reverse_dictionary, glob_config.id_title_map = build_dataset(vocabulary, glob_config.vocabulary_size)
del vocabulary  # Hint to reduce memory.
print('size of data: ' + str(len(glob_config.data)))
print('size of dictionary: ' + str(len(glob_config.dictionary)))
print('size of re-dictionary: ' + str(len(glob_config.reverse_dictionary)))
print('size of id to map dictionary') + str(len(glob_config.id_title_map))
print('Most common words (+UNK)', glob_config.count[:5])
for i in glob_config.data[:10]:
    print('data: ' + str(i) + ' --> word ' +  str(glob_config.reverse_dictionary[i]))
#print('Sample data', glob_config.data[:10], [glob_config.reverse_dictionary[i] for i in glob_config.data[:10]])





data_index = 0

'''
num_skips: number of words drawn randomly from surrounding context
skip_window: the size of the window
span: the size of [skip window + input_word + skip_window]
'''
def generate_batch(batch_size, num_skips, skip_window):
    global data_index
    assert batch_size % num_skips == 0
    assert num_skips <= 2 * skip_window
    batch = np.ndarray(shape=(batch_size), dtype=np.int32)
    context = np.ndarray(shape=(batch_size, 1), dtype=np.int32)
    span = 2 * skip_window + 1
    buffer = collections.deque(maxlen=span)
    for _ in range(span):
        buffer.append(glob_config.data[data_index])
        data_index = (data_index + 1) % len(glob_config.data)  # for loop learning
    for i in range(batch_size // num_skips):
        target = skip_window # target is in the center of the buffer
        targets_visited = [skip_window]
        for j in range(num_skips):
            while target in targets_visited:
                target = random.randint(0, span - 1)
            targets_visited.append(target)
            batch[i * num_skips + j] = buffer[skip_window] # input center word
            context[i * num_skips + j, 0] = buffer[target] # target context word
        buffer.append(glob_config.data[data_index])
        data_index = (data_index + 1) % len(glob_config.data)
        # Backtrack a little bit to avoid skipping words in the end of a batch
    data_index = (data_index + len(glob_config.data) - span) % len(glob_config.data)
    return batch, context


batch, labels = generate_batch(batch_size=8, num_skips=2, skip_window=1)
for i in range(8):
  print(batch[i], glob_config.reverse_dictionary[batch[i]], '->', labels[i, 0],
        glob_config.reverse_dictionary[labels[i, 0]])
