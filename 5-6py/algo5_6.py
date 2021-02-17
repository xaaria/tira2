# 5-6
import itertools as iter
import sys



# ---
def edgeCompare(e1, e2):
  if e1[0] == e2[0]:
    return e1[1] > e2[1]
  else:
    e1[0] > e2[0]

def maxCapacity(edgeSet):

  solutions = [] # All possible solutions
  solutionSize = 0

  for comb in list(iter.product(*edgeSet)):
    for perm in iter.permutations(comb):
      #print(perm)
      rNodes = []
      solution = [] # eges
      
      #for orders in iter.permutations(comb):
      for edge in perm:
        print( "\t{}".format(edge) )
        # if right node is alredy included (lefts are always unique)
        if edge[1] not in rNodes:
          solution.append(edge)
          rNodes.append(edge[1])
        else:
          pass
          #print("\t\tCannot add {}".format(edge))
          # skip this edge

      # Optimal set of nodes is now calculated  
      # #print("solution for this combination is ({}) {} ".format(len(solution), solution))
      
  
      # if its size is bigger than prev. found biggest, clear old solutions and update size
      if len(solution) > solutionSize:
        solutions.clear()
        solutionSize = len(solution)

      # Save the solution we just found 
      if len(solution) == solutionSize:
        solutions.append( solution )

  # Finally, return the result
  return solutions




#for s in sol:
  #print( "\t{}".format(s) )




if __name__ == "__main__":


  # Step 1 - read input
  filename = None
  if len(sys.argv) >= 2:
    filename = sys.argv[1]
  else:
    print("Filename is missing!")
    sys.exit(0)


  # Init variables
  edges_ = dict()
  edges = []

  # Read data
  fo  = open(filename, "r") 
  for line in fo.read().split('\n'):
    if(len(line)>0): # skip empty 
      parts = list(map(lambda x: int(x), line.split(" ")))
      e = (parts[0], parts[1])
      
      # Add key and [] if not already
      if edges_.get(parts[0]) == None:
        edges_[parts[0]] = [] # add new
        #print("new key {}".format(parts[0]) )

      edges_[parts[0]].append( e )

      #edges_[parts[0]].update(e) 
      #print(edges_)
    

  fo.close()
  #print( edges_ )


  for key in edges_:
    edges_[key].reverse()
    edges.append( edges_[key] )
    # print(edges_[key])

  #print( edges )

  """
  edges   = [ 
    [(1,7), (1,6)],
    [(2,7)],
    [(3,9), (3,8), (3,6)],
    [(4,10), (4,7)],
    [(5,10), (5,7)]
  ]
  """ 


  sol = maxCapacity(edges)
  s = sorted(sol, key=lambda e: (e[0], e[1]), reverse=True )
  if(len(s) > 1):
    solution = sorted(s[0], key=lambda e: (e[0], e[1]), )
    print( "Found a maximum matching with {} pairs:".format( len(solution) ) )
    for edge in solution:
      print( "{} {}".format(edge[0], edge[1]) )