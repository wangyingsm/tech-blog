package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
)

type Node struct {
	Childrens int
	Metas     int
	Children  []*Node
	MetaData  []int
}

func input(filename string) []int {
	fp, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Can not read day8 txt file, %v", err)
		os.Exit(-1)
	}
	var result []int
	reader := bufio.NewScanner(fp)
	reader.Split(bufio.ScanWords)
	for reader.Scan() {
		value, _ := strconv.Atoi(reader.Text())
		result = append(result, value)
	}
	fp.Close()
	return result
}

func makeTree(data []int) (*Node, []int) {
	if len(data) == 0 {
		return nil, nil
	}
	node := new(Node)
	node.Childrens, node.Metas = data[0], data[1]
	data = data[2:]
	for i := 0; i < node.Childrens; i++ {
		var nptr *Node
		nptr, data = makeTree(data)
		node.Children = append(node.Children, nptr)
	}
	node.MetaData = append(node.MetaData, data[:node.Metas]...)
	return node, data[node.Metas:]
}

func sumSlice(slice []int) int {
	if len(slice) == 1 {
		return slice[0]
	}
	return slice[0] + sumSlice(slice[1:])
}

func question1(n *Node) int {
	if n.Childrens == 0 {
		return sumSlice(n.MetaData)
	}
	sum := 0
	for _, c := range n.Children {
		sum += question1(c)
	}
	return sum + sumSlice(n.MetaData)
}

func question2(n *Node) int {
	if n.Childrens == 0 {
		return sumSlice(n.MetaData)
	}
	sum := 0
	for _, m := range n.MetaData {
		if m > 0 && m <= n.Childrens {
			sum += question2(n.Children[m-1])
		}
	}
	return sum
}

func main() {
	data := input("day8.txt")
	root, _ := makeTree(data)
	fmt.Println("Question #1:", question1(root))
	fmt.Println("Question #2:", question2(root))
}
