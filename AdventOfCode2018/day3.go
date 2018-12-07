package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type rectangle struct {
	id     int
	left   int
	top    int
	width  int
	height int
}

var fabrics [][]int

func initFabrics() {
	fabrics = make([][]int, 1000)
	for row := range fabrics {
		fabrics[row] = make([]int, 1000)
	}
}

func input(filename string) []rectangle {
	var result []rectangle
	fp, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Can not read day3 input file, %v", err)
		os.Exit(-1)
	}
	reader := bufio.NewScanner(fp)
	for reader.Scan() {
		parts := strings.Split(reader.Text(), " @ ")
		id, _ := strconv.Atoi(parts[0][1:])
		parts = strings.Split(parts[1], ": ")
		lt := strings.Split(parts[0], ",")
		wh := strings.Split(parts[1], "x")
		left, _ := strconv.Atoi(lt[0])
		top, _ := strconv.Atoi(lt[1])
		width, _ := strconv.Atoi(wh[0])
		height, _ := strconv.Atoi(wh[1])
		rec := new(rectangle)
		rec.id, rec.left, rec.top, rec.width, rec.height = id, left, top, width, height
		result = append(result, *rec)
	}
	return result
}

func question1(recs []rectangle) int {
	for _, rec := range recs {
		for r := rec.top; r < rec.top+rec.height; r++ {
			for c := rec.left; c < rec.left+rec.width; c++ {
				fabrics[r][c]++
			}
		}
	}
	count := 0
	for _, r := range fabrics {
		for _, c := range r {
			if c > 1 {
				count++
			}
		}
	}
	return count
}

func question2(recs []rectangle) int {
	for _, rec := range recs {
		found := true
	inner:
		for r := 0; r < rec.height; r++ {
			for c := 0; c < rec.width; c++ {
				if fabrics[rec.top+r][rec.left+c] > 1 {
					found = false
					break inner
				}
			}
		}
		if found {
			return rec.id
		}
	}
	return -1
}

func main() {
	initFabrics()
	recs := input("day3.txt")
	fmt.Println("Question #1:", question1(recs))
	fmt.Println("Question #2:", question2(recs))
}
