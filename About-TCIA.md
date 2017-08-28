# What is TCIA?
# 什么是TCIA？

TCIA is a service which de-identifies and hosts a large archive of medical images of cancer accessible for public download. 
TICA是一个公开的可下载的海量癌症医学图像反识别和归档服务。
The data are organized as “Collections”, typically patients related by a common disease (e.g. lung cancer), image modality (MRI, CT, etc) or research focus. 
数据被组织为“数据集”的方式，归类的方式主要是一种常见的疾病（如肺癌）、图像检查的类型（MRI，cT等）或研究的重点。
DICOM is the primary file format used by TCIA for image storage. 
DICOM是TCIA影像存储的首选文件格式。
Supporting data related to the images such as patient outcomes, treatment details, genomics, pathology, and expert analyses are also provided when available.
还可支持影像的关联数据，例如治疗结果、治疗方法细节、基因组学、病理学和专家诊断。

New Collection proposals (primary data) are reviewed by the TCIA Advisory Group. 
新的数据集（主要的数据）会提交给TCIA顾问委员会进行审核。
If approved, the Data Collection Center (DCC) provides hands-on support to image providers to de-identify and curate their data. 
如果通过，数据集合中心（DCC）会为影像的提供者提供其数据反识别和管理的详细支持。
After the data has been processed it is made available in four different ways for users to access:
数据处理完成后，会提供四种不同的方式给用户获取：

Collection summary pages can be accessed from the home page which provide a detailed explanation of each data set as well as direct download links to quickly obtain all images and supporting data for a given Collection.
数据集的摘要页面会提供每个数据集的详细解释和一个直接的下载链接，以便用户能很快的获得所有的图像和响应的数据。
The Data Portal provides more advanced searching, browsing and filtering capabilities to select image subsets or download images from multiple Collections which meet search criteria.
数据的Portal页面提供更高级的搜索、浏览和过滤功能，用于选择影像的子集或从多个满足搜索条件的数据集中下载影像响。
The Programmatic Interface (REST API) allows software developers to build access to TCIA data into their scripts and applications.
可编程的接口（REST接口）允许软件开发者能编写脚本或应用程序访问TCIA。
TCIA also encourages the creation of Data Analysis Centers (DACs) which provide additional capabilities for visualizing or analyzing TCIA data by connecting to our TCIA Programmatic Interface (REST API) or by mirroring our Collections.
TCIA也鼓励创建数据分析中心（DAC），用来提供对TCIA数据额外的展现和分析能力，通过TCIA可编程的接口（REST接口）或直接镜像TCIA数据集。

To enhance the value of TCIA’s primary data collections we also encourage the research community to publish their analysis results. 
我们也鼓励研究社区发表他们的分析结果，这样可以增加TCIA主要数据集的价值。
Potential analyses could include tumor segmentations, radiomics features, derived/reprocessed images, and radiologist assessments.  
可能的分析包括肿瘤分期、放射性特征、处理后图像和放射学家诊断。
You can view the analyses published by other TCIA users in our Analysis Results directory.
你可以在分析结果目录中分析其他TCIA用户发表的分析结果。

## What value will it be to me?
## 对我来说有什么价值？

A huge amount of clinical and research images are collected each year. 
每年都有海量的临床和研究图像被收录进来。
TCIA organizes and catalogs the images so that they may be used by the research community for a variety of purposes.
TCIA将这些影像进行组织和分类，是的他们可以被研究社区已多种用途进行研究。

Cancer researchers can use this data to test new hypotheses and develop new analysis techniques to advance our scientific understanding of cancer.
癌症研究人员可以使用这些数据来验证新的假设，以及发展新的分析技术提高我们对癌症的科学认识。
Engineers and developers can build new analysis tools and techniques using this data as test material for developing and validating algorithms.
工程师和开发者可以使用这些数据来开发和验证算法，以创建新的分析工具。
Professors can use it as a teaching tool for introducing students to medical imaging technology and cancer phenotypes.
教授们可以使用这些数据来作为给学生介绍医学影像技术和癌症表征的教学工具。
The general public can see how cancer appears in diagnostic images and learn about the instruments doctor uses to diagnose cancer and measure the success of treatment.
普通大众可以看到癌症在医学影像中的表现，并学习到医生用来诊断癌症的指标及衡量疗效的标准。

