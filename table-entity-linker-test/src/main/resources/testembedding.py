import json
import numpy as np

with open("embeddingTest.json", 'w') as f:
	dict_ = {}
	dict_['size'] = 100
	dict_['dimension'] = 300
	dict_['vectors'] = dict(zip(range(100), np.random.randn(100, 300).tolist()))
	json.dump(dict_, f)
