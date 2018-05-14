import json
import matplotlib
matplotlib.use('agg')
import matplotlib.pyplot as plt
import numpy as np
import collections

with open('/websail/jijun/data/statistic.json') as file:
	data = json.load(file)

def plot_img(dict_name, min=0, max=100):
	dict = data[dict_name]
	print('for ' + dict_name + ' :')
	
	X_dict_string = [int(x) for x in dict.keys()]
	Y_dict_string = dict.values()
	
	sum = np.sum(Y_dict_string)
	print('the total number of cell is: ' + str(sum))

	Y_dict_string = [y for _, y in sorted(zip(X_dict_string, Y_dict_string))]
	X_dict_string = sorted(X_dict_string) 

	percent_points = []
	
	acc = 0
	for x in X_dict_string:
		acc += dict[str(x)]
		percent_points.append(acc * 100.0 / sum)
	
	plt.bar(X_dict_string, np.log(Y_dict_string), color='g')
	plt.title(dict_name)	
	plt.ylabel('log(y)')
	plt.xlim((min, max))
	plt.ylabel('log(y)')
	plt.savefig(dict_name + '.png')
	plt.close()

	plt.plot(X_dict_string, percent_points, 'o-')
	plt.title('accumulate percentage')
	plt.ylabel('%')
	plt.xlim((min, max))
	plt.savefig(dict_name + '%.png')
	plt.close()

plot_img('lengthStringMap', min = -1, max = 50)
plot_img('lengthSurfaceMap', min = -1, max = 50)
plot_img('lengthWithOutSurfaceMap', min = -1, max = 50)

print(len(data['lengthStringMap']))
print(len(data['lengthSurfaceMap']))
print(len(data['lengthWithOutSurfaceMap']))


