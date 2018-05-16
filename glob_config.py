import numpy as np


training_data_path = '/websail/jijun/data/first1000marked_row.txt'
out_log_path = '/websail/jijun/out'
id_title_map_path = '/websail/jijun/data/en_id_title.map'


vocabulary_size = 100

data = []
count = []
id_title_map = {}
dictionary = {}
reverse_dictionary = {}
batch_size = 128
embedding_size = 300  # Dimension of the embedding vector.
skip_window = 4  # How many words to consider left and right.
num_skips = 8  # How many times to reuse an input to generate a label.
num_sampled = 64  # Number of negative examples to sample.
num_steps = 1000


# displaying model accuracy, they don't affect calculation.
valid_size = 16  # Random set of words to evaluate similarity on.
valid_window = 100  # Only pick dev samples in the head of the distribution.
valid_examples = np.random.choice(valid_window, valid_size, replace=False)
