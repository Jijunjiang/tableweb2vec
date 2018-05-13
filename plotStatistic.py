import json
import matplotlib.pyplot as plt
import numpy as np
import collections

with open('statistic.json') as file:
	data = json.load(file)

def plot_img(dict_name):
	dict = data[dict_name]
	X_dict_string = [int(x) for x in dict.keys()]
	Y_dict_string = dict.values()

	Y_dict_string = [y for _, y in sorted(zip(X_dict_string, Y_dict_string))]
	X_dict_string = sorted(X_dict_string) 
	plt.bar(X_dict_string, Y_dict_string, color='g')
	plt.title(dict_name)
	plt.savefig(dict_name + '.png')
plot_img('lengthStringMap')
plot_img('lengthSurfaceMap')
plot_img('lengthWithOutSurfaceMap')



