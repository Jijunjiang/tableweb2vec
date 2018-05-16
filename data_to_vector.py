from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import extract_columns_data

import math
import glob_config
import os
import argparse
import json
from tensorflow.contrib.tensorboard.plugins import projector
from tempfile import gettempdir

# Give a folder path as an argument with '--log_dir' to save
# TensorBoard summaries. Default is a log folder in current directory.
current_path = glob_config.out_log_path

parser = argparse.ArgumentParser()
parser.add_argument(
    '--log_dir',
    type=str,
    default=os.path.join(current_path, 'log'),
    help='The log directory for TensorBoard summaries.')
FLAGS, unparsed = parser.parse_known_args()

# Create the directory for TensorBoard variables if there is not.
if not os.path.exists(FLAGS.log_dir):
  os.makedirs(FLAGS.log_dir)

graph = tf.Graph()

with graph.as_default():

    with tf.name_scope('inputs'):
        train_inputs = tf.placeholder(tf.int32, shape=[glob_config.batch_size])
        train_labels = tf.placeholder(tf.int32, shape=[glob_config.batch_size, 1])
        valid_dataset = tf.constant(glob_config.valid_examples, dtype=tf.int32)

    with tf.device('/cpu:0'):
        with tf.name_scope('embeddings'):
            embeddings = tf.Variable(tf.random_uniform([glob_config.vocabulary_size, glob_config.embedding_size], -1.0, 1.0))
            embed = tf.nn.embedding_lookup(embeddings, train_inputs)

        with tf.name_scope('weights'):
            nce_weights = tf.Variable(
                tf.truncated_normal(
                    [glob_config.vocabulary_size, glob_config.embedding_size],
                    stddev=1.0 / math.sqrt(glob_config.embedding_size)))

        with tf.name_scope('biases'):
            nce_biases = tf.Variable(tf.zeros([glob_config.vocabulary_size]))

        # tf.nce loss auto draws a new sample of negative each time evaluate the loss
        with tf.name_scope('loss'):
            loss = tf.reduce_mean(
                tf.nn.nce_loss(weights=nce_weights,
                               biases=nce_biases,
                               labels=train_labels,
                               inputs=embed,
                               num_sampled=glob_config.num_sampled,
                               num_classes=glob_config.vocabulary_size))

        tf.summary.scalar('loss', loss)

        # construct SGD optimizer
        with tf.name_scope('optimizer'):
            optimizer = tf.train.GradientDescentOptimizer(1.0).minimize(loss)

        # compute the cos similarity between minibatch examples and all embeddings
        norm = tf.sqrt(tf.reduce_sum(tf.square(embeddings), 1, keep_dims=True))
        normalized_embeddings = embeddings / norm
        valid_embeddings = tf.nn.embedding_lookup(normalized_embeddings, valid_dataset)

        similarity = tf.matmul(valid_embeddings, normalized_embeddings, transpose_b=True)

        # Merge all summaries.
        merged = tf.summary.merge_all()

        # Add variable initializer.
        init = tf.global_variables_initializer()

        # Create a saver.
        saver = tf.train.Saver()

num_steps = glob_config.num_steps

with tf.Session(graph=graph) as session:
    writer = tf.summary.FileWriter(FLAGS.log_dir, session.graph)

    # initialize all the variables
    init.run()
    print('Initialized')

    average_loss = 0
    for step in xrange(num_steps):
        batch_inputs, batch_labels = extract_columns_data.generate_batch(glob_config.batch_size, glob_config.num_skips, glob_config.skip_window)
        feed_dict = {train_inputs: batch_inputs, train_labels: batch_labels}

        # Define metadata variable
        run_metadata = tf.RunMetadata()

        _, summary, loss_val = session.run(
            [optimizer, merged, loss],
            feed_dict=feed_dict,
            run_metadata=run_metadata)
        average_loss += loss_val

        writer.add_summary(summary, step)
        if step == (num_steps - 1):
            writer.add_run_metadata(run_metadata, 'step%d' % step)

        if step % 2000 == 0:
            if step > 0:
                average_loss /= 2000
            print ('Average loss at step', step, ': ', average_loss)
            average_loss = 0

        # this is expensive
        if step % 10000 == 0:
            sim = similarity.eval()
            for i in xrange(glob_config.valid_size):
                valid_word = glob_config.reverse_dictionary[glob_config.valid_examples[i]]
                top_k = 8
                nearest = (-sim[i, :]).argsort()[1:top_k + 1]
                log_str = 'Nearest to %s:' % valid_word
                for k in xrange(top_k):
                    close_word = glob_config.reverse_dictionary[nearest[k]]
                    if close_word[0] == 'e' and close_word[1:].isdigit() and (close_word[1:] in glob_config.id_title_map.keys()):
                        close_word = 'Entity ' + glob_config.id_title_map[close_word[1:]]
                    log_str = '%s %s,' % (log_str, close_word)
                print(log_str)
    final_embeddings = normalized_embeddings.eval()

    # Write corresponding labels for the embeddings.
    with open(FLAGS.log_dir + '/metadata.tsv', 'w') as f:
        for i in xrange(glob_config.vocabulary_size):
            f.write(glob_config.reverse_dictionary[i] + '\n')

    # save model for checkpoints
    saver.save(session, os.path.join(FLAGS.log_dir, 'model.ckpt'))

    config = projector.ProjectorConfig()
    embedding_conf = config.embeddings.add()
    embedding_conf.tensor_name = embeddings.name
    embedding_conf.metadata_path = os.path.join(FLAGS.log_dir, 'metadata.tsv')
    projector.visualize_embeddings(writer, config)

    dict_cout = {}
    size = len(final_embeddings)
    dimension = len(final_embeddings[0])
    dict_final_embeddings = dict((glob_config.reverse_dictionary[i], final_embeddings[i].tolist()) for i in range(glob_config.vocabulary_size))
    dict_cout['size'] = size
    dict_cout['dimension'] = dimension
    dict_cout['vectors'] = dict_final_embeddings
    with open(os.path.join(FLAGS.log_dir, 'embedding_data.json'), 'w') as f:
        json.dump(dict_cout, f)

writer.close()


# Step 6: Visualize the embeddings.

# pylint: disable=missing-docstring
# Function to draw visualization of distance between embeddings.
def plot_with_labels(low_dim_embs, labels, filename):
    assert low_dim_embs.shape[0] >= len(labels), 'More labels than embeddings'
    plt.figure(figsize=(18, 18))  # in inches
    for i, label in enumerate(labels):
        x, y = low_dim_embs[i, :]
        plt.scatter(x, y)
        plt.annotate(
            label,
            xy=(x, y),
            xytext=(5, 2),
            textcoords='offset points',
            ha='right',
            va='bottom')

    plt.savefig(filename)


try:
    # pylint: disable=g-import-not-at-top
    from sklearn.manifold import TSNE
    import matplotlib.pyplot as plt

    tsne = TSNE(
        perplexity=30, n_components=2, init='pca', n_iter=5000, method='exact')
    plot_only = 500
    low_dim_embs = tsne.fit_transform(final_embeddings[:plot_only, :])
    labels = [glob_config.reverse_dictionary[i] for i in xrange(plot_only)]
    plot_with_labels(low_dim_embs, labels, os.path.join(current_path, 'tsne.png'))

except ImportError as ex:
    print('Please install sklearn, matplotlib, and scipy to show embeddings.')
    print(ex)











