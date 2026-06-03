export interface Category {
  id: string;
  name: string;
  description?: string;
  parentId: string | null;
}

// Used specifically for the UI to handle the nested tree view
export interface CategoryNode extends Category {
  children: CategoryNode[];
  level: number;
  expanded: boolean;
}