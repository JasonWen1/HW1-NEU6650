import matplotlib.pyplot as plt


languages = ["Go", "Java"]
data_sizes = [9856.26, 9967.17]  

plt.bar(languages, data_sizes, color=['blue', 'green'])
plt.xlabel('Languages')
plt.ylabel('Data Size')
plt.title('10-30-2 throughput comparation')


plt.show()