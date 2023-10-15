import matplotlib.pyplot as plt
import pandas as pd

df = pd.read_csv("Go_10_30_2.csv")
df['startTime'] = (df['startTime'] - df['startTime'].min()) // 1000
throughput = df.groupby('startTime').size()
plt.plot(throughput.index, throughput.values)
plt.xlabel('Time (seconds)')
plt.ylabel('Requests/Second')
plt.title('Throughput over Time')
plt.grid(True)
plt.show()

